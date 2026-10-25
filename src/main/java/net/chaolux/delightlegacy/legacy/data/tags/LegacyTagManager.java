package net.chaolux.delightlegacy.legacy.data.tags;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.registry.GameRegistry;
import net.chaolux.delightlegacy.DelightLegacy;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import javax.xml.transform.Result;
import java.security.Key;
import java.util.*;

public class LegacyTagManager {
    private static final LegacyTagManager INSTANCE=new LegacyTagManager();
    private volatile Map<Key, Result> keyResultMap= Collections.emptyMap();
    public static LegacyTagManager getInstance() {
        return INSTANCE;
    }

    public synchronized void reload() {
        Map<Key, List<Entry>> keyListMap=new LinkedHashMap<Key,List<Entry>>();
        int value=0;
        for (LegacyTagLoader.EntryWithSource entryWithSource : LegacyTagLoader.list()) {
            try {
                Key key=new Key(entryWithSource.tag,entryWithSource.resourceLocation);
                if(!entryWithSource.jsonObject.has("values") || !entryWithSource.jsonObject.get("values").isJsonArray()) throw new IllegalArgumentException("Missing values array");
                List<Entry> entryList=keyListMap.get(key);
                if(entryList == null || (entryWithSource.jsonObject.has("replace") && entryWithSource.jsonObject.get("replace").getAsBoolean())) {
                    entryList=new ArrayList<Entry>();
                    keyListMap.put(key,entryList);
                }
                JsonArray jsonArray=entryWithSource.jsonObject.getAsJsonArray("values");
                for (JsonElement jsonElement : jsonArray) {
                    entryList.add(parse(jsonElement));
                }
            } catch (Exception exception) {
                value++;
                DelightLegacy.LOGGER.warn("Invalid tag {}: {}",entryWithSource.string,exception.getMessage());
            }
        }
        Map<Key,Result> resultMap=new LinkedHashMap<Key,Result>();
        Set<String> stringSet=new HashSet<String>();
        for (Key key : keyListMap.keySet()) {
            resolve(key,keyListMap,resultMap,new LinkedHashSet<Key>(),stringSet);
        }
        keyResultMap=Collections.unmodifiableMap(resultMap);
        DelightLegacy.LOGGER.info("Load {} legacy tag",keyResultMap.size());
    }

    public boolean isItem(ResourceLocation resourceLocation, ItemStack itemStack) {
        if(resourceLocation == null || itemStack == null) return false;
        Result result=keyResultMap.get(new Key(Tag.ITEMS,resourceLocation));
        if(result != null) {
            for (ItemStack stack : result.itemStackList) {
                if(OreDictionary.itemMatches(stack,itemStack,false)) return true;
            }
            for (ResourceLocation location : result.resourceLocationSet) {
                if(OreBridge.isItems(location,itemStack)) return true;
            }
        }
        return OreBridge.isItems(resourceLocation,itemStack);
    }

    public List<ItemStack> getItem(ResourceLocation resourceLocation) {
        if(resourceLocation == null) return Collections.emptyList();
        List<ItemStack> itemStackList=new ArrayList<ItemStack>();
        Result result=keyResultMap.get(new Key(Tag.ITEMS,resourceLocation));
        if(result != null) {
            for (ItemStack itemStack : result.itemStackList) {
                addList(itemStackList,itemStack);
            }
            for (ResourceLocation location : result.resourceLocationSet) {
                for (ItemStack itemStack : OreBridge.getItem(location)) {
                    addList(itemStackList,itemStack);
                }
            }
        }
        for (ItemStack itemStack : OreBridge.getItem(resourceLocation)) {
            addList(itemStackList,itemStack);
        }
        return Collections.unmodifiableList(itemStackList);
    }

    public boolean isBlock(ResourceLocation resourceLocation, Block block,int value) {
        if(resourceLocation == null || block == null) return false;
        Result result=keyResultMap.get(new Key(Tag.BLOCKS,resourceLocation));
        if(result != null) {
            for (BlockEntry blockEntry : result.blockEntryList) {
                if (blockEntry.block == block && (blockEntry.value == OreDictionary.WILDCARD_VALUE || blockEntry.value == value))
                    return true;
            }
            for (ResourceLocation location : result.resourceLocationSet) {
                if (OreBridge.isBlock(location, block, value)) return true;
            }
        }
        return OreBridge.isBlock(resourceLocation,block,value);
    }

    public boolean isEntity(ResourceLocation resourceLocation, Entity entity) {
        if(resourceLocation == null || entity == null) return false;
        Result result=keyResultMap.get(new Key(Tag.ENTITIES,resourceLocation));
        if(result == null) return false;
        String string= EntityList.getEntityString(entity);
        if(string == null) return false;
        String data=dataEntity(string);
        for(String entities : result.stringSet) {
            if(entity instanceof EntityHorse && "minecraft:horse".equals(entities)) {
                if (((EntityHorse)entity).getHorseType() == 0) return true;
                continue;
            }
            if(setEntity(entities).equals(setEntity(data))) return true;
        }
        return false;
    }

    private static Result resolve(Key key,Map<Key,List<Entry>> keyListMap,Map<Key,Result> keyResultMap,Set<Key> keySet,Set<String> stringSet) {
        Result result=keyResultMap.get(key);
        if(result != null) return result;
        Result results=new Result();
        if(!keySet.add(key)) return results;
        try {
            List<Entry> entryList = keyListMap.get(key);
            if (entryList != null) {
                for (Entry entry : entryList) {
                    if (entry.metadata) {
                        Key keys = new Key(key.tag, entry.resourceLocation);
                        if (keyListMap.containsKey(keys)) {
                            results.add(resolve(keys, keyListMap, keyResultMap, keySet, stringSet));
                        } else if (!OreBridge.isItem(entry.resourceLocation) && entry.data) {
                            if (stringSet.add("Missing tag #" + entry.resourceLocation + " in " + key))
                                DelightLegacy.LOGGER.warn("Missing required tag #{} in {}", entry.resourceLocation, key);
                        }
                        if (key.tag != Tag.ENTITIES && OreBridge.isItem(entry.resourceLocation))
                            results.resourceLocationSet.add(entry.resourceLocation);
                        continue;
                    }
                    if (key.tag == Tag.ITEMS) {
                        Item item = GameRegistry.findItem(ResourceLocations.namespace(entry.resourceLocation), ResourceLocations.path(entry.resourceLocation));
                        if (item == null) {
                            if (entry.data) {
                                DelightLegacy.LOGGER.warn("Missing required item " + entry.resourceLocation + " in " + key);
                            }
                        } else {
                            addList(results.itemStackList, new ItemStack(item, 1, entry.value));
                        }
                    } else if (key.tag == Tag.BLOCKS) {
                        Block block = GameRegistry.findBlock(ResourceLocations.namespace(entry.resourceLocation), ResourceLocations.path(entry.resourceLocation));
                        if (block == null) {
                            if (entry.data) {
                                DelightLegacy.LOGGER.warn("Missing required block " + entry.resourceLocation + " in " + key);
                            }
                        } else {
                            results.addBlock(new BlockEntry(block, entry.value));
                        }
                    } else {
                        results.stringSet.add(ResourceLocations.getString(entry.resourceLocation));
                    }
                }
            }
        } finally {
            keySet.remove(key);
        }
        keyResultMap.put(key,results);
        return results;
    }

    private static Entry parse(JsonElement jsonElement) {
        String string;
        boolean data=true;
        int value=OreDictionary.WILDCARD_VALUE;
        if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
            string=jsonElement.getAsString();
        } else if(jsonElement.isJsonObject()) {
            JsonObject jsonObject=jsonElement.getAsJsonObject();
            if(!jsonObject.has("id")) throw new IllegalArgumentException("Tag object without id");
            string=jsonObject.get("id").getAsString();
            if(jsonObject.has("required")) data=jsonObject.get("required").getAsBoolean();
            if (jsonObject.has("data")) value=jsonObject.get("data").getAsInt();
        } else {
            throw new IllegalArgumentException("Tag must be ID");
        }
        if(value < 0 && value != OreDictionary.WILDCARD_VALUE) throw new IllegalArgumentException("Invalid metadata " + value);
        boolean metadata=string.startsWith("#");
        ResourceLocation resourceLocation=ResourceLocations.parse(metadata ? string.substring(1) : string);
        return new Entry(resourceLocation,value,data,metadata);
    }

    private static void addList(List<ItemStack> itemStackList,ItemStack itemStack) {
        for(ItemStack stack : itemStackList) {
            if(stack.getItem() == itemStack.getItem() && stack.getItemDamage() == itemStack.getItemDamage()) return;
        }
        itemStackList.add(itemStack.copy());
    }

    private static String dataEntity(String string) {
        String data=string.toLowerCase(Locale.ROOT);
        return data.indexOf(':') >= 0 ? data : "minecraft:" + data;
    }

    private static String setEntity(String string) {
        return string.toLowerCase(Locale.ROOT).replace("_","");
    }

    private static final class Key {
        final Tag tag;
        final ResourceLocation resourceLocation;
        Key(Tag tag,ResourceLocation resourceLocation) {
            this.tag=tag;
            this.resourceLocation=resourceLocation;
        }

        @Override
        public boolean equals(Object object) {
            if(!(object instanceof Key)) return false;
            Key key=(Key) object;
            return tag == key.tag && resourceLocation.equals(key.resourceLocation);
        }

        @Override
        public int hashCode() {
            return 31 * tag.hashCode() + resourceLocation.hashCode();
        }

        @Override
        public String toString() {
            return tag.getString() + ":" + ResourceLocations.getString(resourceLocation);
        }
    }

    private static final class Entry {
        final ResourceLocation resourceLocation;
        final int value;
        final boolean data;
        final boolean metadata;
        Entry(ResourceLocation resourceLocation,int value,boolean data,boolean metadata) {
            this.resourceLocation=resourceLocation;
            this.value=value;
            this.data=data;
            this.metadata=metadata;
        }
    }

    private static final class BlockEntry {
        final Block block;
        final int value;
        BlockEntry(Block block,int value) {
            this.block=block;
            this.value=value;
        }
    }

    private static final class Result {
        final List<ItemStack> itemStackList=new ArrayList<ItemStack>();
        final List<BlockEntry> blockEntryList=new ArrayList<BlockEntry>();
        final Set<String> stringSet=new LinkedHashSet<String>();
        final Set<ResourceLocation> resourceLocationSet=new LinkedHashSet<ResourceLocation>();
        void add(Result result) {
            for (ItemStack itemStack : result.itemStackList) {
                addList(itemStackList,itemStack);
            }
            for (BlockEntry blockEntry : result.blockEntryList) {
                addBlock(blockEntry);
            }
            stringSet.addAll(result.stringSet);
            resourceLocationSet.addAll(result.resourceLocationSet);
        }

        void addBlock(BlockEntry blockEntry) {
            for (BlockEntry entry : blockEntryList) {
                if(entry.block == blockEntry.block && entry.value == blockEntry.value) return;
            }
            blockEntryList.add(blockEntry);
        }
    }
}
