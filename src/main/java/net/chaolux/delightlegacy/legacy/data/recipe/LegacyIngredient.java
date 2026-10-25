/*
Copyright (c) Forge Development LLC and contributors
SPDX-License-Identifier: LGPL-2.1-only
Modified by Chaolux, 2026
 */
package net.chaolux.delightlegacy.legacy.data.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.registry.GameRegistry;
import net.chaolux.delightlegacy.common.utility.ItemUtils;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.chaolux.delightlegacy.legacy.data.tags.LegacyTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LegacyIngredient {
    public static final LegacyIngredient EMPTY=new LegacyIngredient(Collections.<Value>emptyList());
    private final List<Value> valueList;
    private LegacyIngredient(List<Value> valueList) {
        this.valueList=Collections.unmodifiableList(new ArrayList<Value>(valueList));
    }

    public static LegacyIngredient fromJson(JsonElement jsonElement) {
        if(jsonElement == null || jsonElement.isJsonNull()) return EMPTY;
        List<Value> values=new ArrayList<Value>();
        if(jsonElement.isJsonArray()) {
            JsonArray jsonArray=jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) values.add(parseValue(element));
        } else {
            values.add(parseValue(jsonElement));
        }
        return values.isEmpty() ? EMPTY : new LegacyIngredient(values);
    }

    public boolean is(ItemStack itemStack) {
        if(itemStack == null) return false;
        for (Value value : valueList) {
            if(value.is(itemStack)) return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return valueList.isEmpty();
    }

    public List<ItemStack> getMatches() {
        List<ItemStack> itemStackList=new ArrayList<ItemStack>();
        for (Value value : valueList) {
            for (ItemStack itemStack : value.getMatches()) {
                if(itemStack != null) itemStackList.add(itemStack.copy());
            }
        }
        return Collections.unmodifiableList(itemStackList);
    }

    public int getValue() {
        return valueList.size();
    }

    private static Value parseValue(JsonElement jsonElement) {
        if(jsonElement == null || !jsonElement.isJsonObject()) throw new JsonSyntaxException("Ingredient must be json object or an array of object");
        JsonObject jsonObject=jsonElement.getAsJsonObject();
        boolean isItem=jsonObject.has("item");
        boolean isOre=jsonObject.has("ore");
        boolean isTag=jsonObject.has("tag");
        boolean isType=jsonObject.has("type");
        int value=(isItem ? 1 : 0) + (isOre ? 1 : 0) + (isTag ? 1 : 0) + (isType ? 1 : 0);
        if(value != 1) throw new JsonSyntaxException("Ingredient must define exactly one of item or ore or tag or type");
        if(isItem) return parseItemValue(jsonObject);
        if(isOre) return parseOreValue(jsonObject);
        if(isTag) return new TagValue(LegacyRecipeJson.getString(jsonObject,"tag"));
        String string=LegacyRecipeJson.getString(jsonObject,"type");
        return new ToolActionValue(LegacyRecipeJson.getString(jsonObject,"action"));
    }

    private static Value parseItemValue(JsonObject jsonObject) {
        ResourceLocation resourceLocation= LegacyRecipeJson.getLocation(jsonObject,"item");
        Item item= GameRegistry.findItem(ResourceLocations.namespace(resourceLocation),ResourceLocations.path(resourceLocation));
        if(item == null) throw new JsonSyntaxException("Unknown ingredient item '" + ResourceLocations.getString(resourceLocation) + "'");
        int data=LegacyRecipeJson.getInt(jsonObject,"data", OreDictionary.WILDCARD_VALUE);
        if(data < 0) throw new JsonSyntaxException("Ingredient data value cannot be negative");
        return new ItemValue(resourceLocation,new ItemStack(item,1,data));
    }

    private static Value parseOreValue(JsonObject jsonObject) {
        String string=LegacyRecipeJson.getString(jsonObject,"ore");
        if(string.isEmpty()) throw new JsonSyntaxException("Ore Dictionary ingredient cannot be empty");
        return new OreValue(string);
    }

    private interface Value {
        boolean is(ItemStack itemStack);
        List<ItemStack> getMatches();
    }

    private static final class ItemValue implements Value {
        private final ResourceLocation resourceLocation;
        private final ItemStack itemStack;
        private ItemValue(ResourceLocation resourceLocation,ItemStack itemStack) {
            this.resourceLocation=resourceLocation;
            this.itemStack=itemStack;
        }

        @Override
        public boolean is(ItemStack stack) {
            return OreDictionary.itemMatches(itemStack,stack,false);
        }

        @Override
        public List<ItemStack> getMatches() {
            return Collections.singletonList(itemStack.copy());
        }

        @Override
        public String toString() {
            return "item:" + ResourceLocations.getString(resourceLocation);
        }
    }

    private static final class OreValue implements Value {
        private final String string;
        private OreValue(String string) {
            this.string=string;
        }

        @Override
        public boolean is(ItemStack itemStack) {
            List<ItemStack> itemStackList=OreDictionary.getOres(string,false);
            for(ItemStack stack : itemStackList) {
                if(OreDictionary.itemMatches(stack,itemStack,false)) return true;
            }
            return false;
        }

        @Override
        public List<ItemStack> getMatches() {
            List<ItemStack> itemStackList=OreDictionary.getOres(string,false);
            List<ItemStack> stackList=new ArrayList<ItemStack>(itemStackList.size());
            for(ItemStack itemStack : itemStackList) {
                if(itemStack != null) stackList.add(itemStack.copy());
            }
            return stackList;
        }

        @Override
        public String toString() {
            return "ore:" + string;
        }
    }

    private static final class TagValue implements Value {
        private final String string;
        private TagValue(String string) {
            if(string == null || string.isEmpty()) throw new JsonSyntaxException("Tag cannot be null");
            this.string=string;
        }

        @Override
        public boolean is(ItemStack itemStack) {
            return LegacyTags.Items.isItem(string,itemStack);
        }

        @Override
        public List<ItemStack> getMatches() {
            return LegacyTags.Items.getItemStack(string);
        }

        @Override
        public String toString() {
            return "tag" + string;
        }
    }

    private static final class ToolActionValue implements Value {
        private final String string;
        private ToolActionValue(String string) {
            if(string == null || string.isEmpty()) throw new JsonSyntaxException("Action cannot be null");
            this.string=string;
        }

        @Override
        public boolean is(ItemStack itemStack) {
            return ItemUtils.isToolAction(itemStack,string);
        }

        @Override
        public List<ItemStack> getMatches() {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return "tool_action:" + string;
        }
    }
}
