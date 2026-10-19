package net.chaolux.delightlegacy.legacy.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;

public class LegacySeedItem extends ItemSeeds {
    public LegacySeedItem(Block block,Block soil,LegacyItemProperties legacyItemProperties) {
        super(block,soil);
        if(legacyItemProperties != null) legacyItemProperties.apply(this);
    }
}
