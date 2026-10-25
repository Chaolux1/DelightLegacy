package net.chaolux.delightlegacy.legacy.data.tags;

import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public class OreBridge {
    private static final Map<ResourceLocation, List<String>> ORE=new LinkedHashMap<ResourceLocation,List<String>>();
    static {
        registerOre("forge:tools/knives","toolKnife");
        registerOre("farmersdelight:tools/knives","toolKnife");
        registerOre("c:tools/knife","toolKnife");
        registerOre("farmersdelight:straw_harvesters","toolKnifeStraw");
        registerOre("farmersdelight:straw_harvesters","toolStrawHarvester");
        registerOre("forge:ingots/iron","ingotIron");
        registerOre("forge:ingots/gold","ingotGold");
        registerOre("forge:nuggets/iron","nuggetIron");
        registerOre("forge:nuggets/gold","nuggetGold");
        registerOre("forge:dough","foodDough");
        registerOre("minecraft:planks","plankWood");
        registerOre("minecraft:logs","logWood");
        registerOre("forge:crops/wheat","cropWheat");
        registerOre("forge:crops/tomato","cropTomato");
        registerOre("forge:crops/onion","cropOnion");
    }

    public static synchronized void registerOre(String string,String ore) {
        ResourceLocation resourceLocation= ResourceLocations.parse(string);
        if(ore == null || ore.isEmpty()) throw new IllegalArgumentException("Ore cannot be null");
        List<String> stringList=ORE.get(resourceLocation);
        if(stringList == null) {
            stringList=new ArrayList<String>();
            ORE.put(resourceLocation,stringList);
        }
        if(!stringList.contains(ore)) stringList.add(ore);
    }

    public static synchronized boolean isItem(ResourceLocation resourceLocation) {
        return ORE.containsKey(resourceLocation);
    }

    public static boolean isItems(ResourceLocation resourceLocation, ItemStack itemStack) {
        if(itemStack == null) return false;
        for(ItemStack stack : getItem(resourceLocation)) {
            if(OreDictionary.itemMatches(stack,itemStack,false)) return true;
        }
        return false;
    }

    public static boolean isBlock(ResourceLocation resourceLocation, Block block,int value) {
        if(block == null) return false;
        Item item=Item.getItemFromBlock(block);
        return item != null && isItems(resourceLocation,new ItemStack(item,1,value));
    }

    public static synchronized List<ItemStack> getItem(ResourceLocation resourceLocation) {
        List<String> stringList=ORE.get(resourceLocation);
        if(stringList == null) return Collections.emptyList();
        List<ItemStack> itemStackList=new ArrayList<ItemStack>();
        for(String string : stringList) {
            for (ItemStack itemStack : OreDictionary.getOres(string,false)) {
                if(itemStack != null) itemStackList.add(itemStack.copy());
            }
        }
        return itemStackList;
    }
}
