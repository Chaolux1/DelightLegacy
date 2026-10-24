package net.chaolux.delightlegacy.legacy.registry;

import net.minecraft.item.ItemBlock;

public interface LegacyItemBlock {
    Class<? extends ItemBlock> getItemBlock();
}
