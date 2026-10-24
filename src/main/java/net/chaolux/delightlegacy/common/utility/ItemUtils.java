package net.chaolux.delightlegacy.common.utility;

import net.chaolux.delightlegacy.common.item.KnifeItem;
import net.chaolux.delightlegacy.common.tag.ModTags;
import net.chaolux.delightlegacy.legacy.common.item.LegacyToolAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemUtils {
    public static boolean isValidTool(ItemStack itemStack,String toolAction,String string) {
        if(itemStack == null || itemStack.getItem() == null) return false;
        Item item=itemStack.getItem();
        if(item instanceof LegacyToolAction && ((LegacyToolAction)item).isToolAction(itemStack,toolAction)) return true;
        return isTag(itemStack,string);
    }

    public static boolean isKnife(ItemStack itemStack) {
        return isValidTool(itemStack, KnifeItem.KNIFE_HARVEST, ModTags.Items.KNIFE) || isTag(itemStack,ModTags.Items.KNIFE_HARVEST);
    }

    public static boolean isCut(ItemStack itemStack) {
        return isValidTool(itemStack,KnifeItem.KNIFE_DIG,ModTags.Items.KNIFE) || isTag(itemStack,ModTags.Items.KNIFE_DIG);
    }

    public static boolean isHarvestStraw(ItemStack itemStack) {
        return isKnife(itemStack) || isTag(itemStack,ModTags.Items.KNIFE_STRAW);
    }

    public static boolean isTag(ItemStack itemStack,String string) {
        if(itemStack == null || itemStack.getItem() == null || string == null || string.isEmpty()) return false;
        for (ItemStack stack : OreDictionary.getOres(string,false)) {
            if(OreDictionary.itemMatches(stack,itemStack,false)) return true;
        }
        return false;
    }
}
