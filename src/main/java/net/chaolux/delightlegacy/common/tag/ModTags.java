package net.chaolux.delightlegacy.common.tag;

import net.chaolux.delightlegacy.common.item.KnifeItem;
import net.chaolux.delightlegacy.legacy.registry.LegacyRegistryObject;
import net.chaolux.delightlegacy.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModTags {
    public static final class Items {
        public static final String KNIFE="toolKnife";
        public static final String KNIFE_DIG="toolKnifeDig";
        public static final String KNIFE_HARVEST="toolKnifeHarvest";
        public static final String KNIFE_STRAW="toolKnifeStraw";
    }

    public static void register() {
        for(LegacyRegistryObject<? extends Item> legacyRegistryObject : ModItems.ITEMS.getObject()) {
            Item item=legacyRegistryObject.getValue();
            if(!(item instanceof KnifeItem)) continue;
            ItemStack itemStack=new ItemStack(item,1, OreDictionary.WILDCARD_VALUE);
            OreDictionary.registerOre(Items.KNIFE_DIG,itemStack);
            OreDictionary.registerOre(Items.KNIFE_HARVEST,itemStack.copy());
        }
    }
}
