package net.chaolux.delightlegacy.common.utility;

import net.chaolux.delightlegacy.common.item.KnifeItem;
import net.chaolux.delightlegacy.common.tag.ModTags;
import net.chaolux.delightlegacy.legacy.common.item.LegacyToolAction;
import net.chaolux.delightlegacy.legacy.data.tags.LegacyTags;
import net.minecraft.item.*;
import net.minecraftforge.oredict.OreDictionary;

public class ItemUtils {
    public static boolean isValidTool(ItemStack itemStack,String toolAction,String string) {
        if(itemStack == null || itemStack.getItem() == null) return false;
        Item item=itemStack.getItem();
        if(item instanceof LegacyToolAction && ((LegacyToolAction)item).isToolAction(itemStack,toolAction)) return true;
        return isTag(itemStack,string);
    }

    public static boolean isKnife(ItemStack itemStack) {
        return LegacyTags.Items.isItem("farmersdelight:tools/knives",itemStack) || LegacyTags.Items.isItem("forge:tools/knives",itemStack) || isValidTool(itemStack,KnifeItem.KNIFE_HARVEST,ModTags.Items.KNIFE_HARVEST);
    }

    public static boolean isCut(ItemStack itemStack) {
        return LegacyTags.Items.isItem("farmersdelight:tools/knives",itemStack) || LegacyTags.Items.isItem("forge:tools/knives",itemStack) || isValidTool(itemStack,KnifeItem.KNIFE_DIG,ModTags.Items.KNIFE_DIG);
    }

    public static boolean isHarvestStraw(ItemStack itemStack) {
        return LegacyTags.Items.isItem("farmersdelight:straw_harvesters",itemStack) || isTag(itemStack,ModTags.Items.KNIFE_STRAW) || isKnife(itemStack);
    }

    public static boolean isTag(ItemStack itemStack,String string) {
        if(itemStack == null || itemStack.getItem() == null || string == null || string.isEmpty()) return false;
        for (ItemStack stack : OreDictionary.getOres(string,false)) {
            if(OreDictionary.itemMatches(stack,itemStack,false)) return true;
        }
        return false;
    }

    public static boolean isToolAction(ItemStack itemStack,String string) {
        if (itemStack == null || itemStack.getItem() == null || string == null) return false;
        Item item = itemStack.getItem();
        if (item instanceof LegacyToolAction && ((LegacyToolAction) item).isToolAction(itemStack, string)) return true;
        if ("knife_dig".equals(string)) return isCut(itemStack);
        if ("knife_harvest".equals(string)) return isKnife(itemStack);
        if ("axe_dig".equals(string) || "axe_strip".equals(string)) return item instanceof ItemAxe || isTag(itemStack, "toolAxe");
        if ("shovel_dig".equals(string)) return item instanceof ItemSpade || isTag(itemStack, "toolShovel");
        if ("pickaxe_dig".equals(string)) return item instanceof ItemPickaxe || isTag(itemStack, "toolPickaxe");
        if ("hoe_dig".equals(string)) return item instanceof ItemHoe || isTag(itemStack, "toolHoe");
        if ("shears_dig".equals(string)) return item instanceof ItemShears || isTag(itemStack, "toolShears");
        return false;
    }
}
