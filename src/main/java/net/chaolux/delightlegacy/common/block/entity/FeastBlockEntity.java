package net.chaolux.delightlegacy.common.block.entity;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class FeastBlockEntity extends SyncedBlockEntity {
    private int value;
    public int getValue() {
        return value;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        value=nbtTagCompound.getByte("Facing") & 3;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setByte("Facing",(byte)value);
    }

    @Override
    public boolean shouldRefresh(Block block, Block blocks, int value, int data, World world,int x,int y,int z) {
        return block != blocks;
    }

    public void setValue(int value) {
        value &= 3;
        if(this.value == value) return;
        this.value=value;
        inventoryChanged();
    }
}
