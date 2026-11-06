package net.chaolux.delightlegacy.common.block.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class SyncedBlockEntity extends TileEntity {
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTagCompound=new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,1,nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager networkManager,S35PacketUpdateTileEntity s35PacketUpdateTileEntity) {
        readFromNBT(s35PacketUpdateTileEntity.func_148857_g());
        if(worldObj != null) worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
    }

    protected void inventoryChanged() {
        markDirty();
        if(worldObj != null && !worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
            worldObj.func_147453_f(xCoord,yCoord,zCoord,getBlockType());
        }
    }
}
