package net.chaolux.delightlegacy.common.item;

import net.chaolux.delightlegacy.common.block.FeastBlock;
import net.chaolux.delightlegacy.common.utility.TextUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PlaceableItem extends ItemBlock {
    public PlaceableItem(Block block) {
        super(block);
        if(block instanceof FeastBlock) setMaxStackSize(1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean value) {
        list.add(TextUtils.PLACEABLE);
    }
}
