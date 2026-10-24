package net.chaolux.delightlegacy.common.item;

import net.chaolux.delightlegacy.common.block.MushroomColonyBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class MushroomColonyItem extends ItemBlock {
    public MushroomColonyItem(Block block) {
        super(block);
    }

    @Override
    public int getMetadata(int value) {
        return MushroomColonyBlock.COLONY_AGE;
    }
}
