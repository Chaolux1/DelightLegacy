package net.chaolux.delightlegacy.common.block.entity;

import net.chaolux.delightlegacy.common.block.CabinetBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CabinetBlockEntity extends SyncedBlockEntity implements ISidedInventory {
    private static final int INVENTORY_SIZE=27;
    private static final int[] SLOT=new int[INVENTORY_SIZE];
    private final ItemStack[] inventory=new ItemStack[INVENTORY_SIZE];
    private String customName;
    private int openersCounter;
    private int value;
    static {
        for(int index=0;index < SLOT.length;index++) {
            SLOT[index]=index;
        }
    }

    @Override
    public void updateEntity() {
        if(worldObj == null || worldObj.isRemote || ++value % 20 != 0) return;
        if(worldObj.getBlock(xCoord,yCoord,zCoord) != getBlockType()) return;
        int data=0;
        for(Object object : worldObj.playerEntities) {
            if(!(object instanceof EntityPlayer)) continue;
            EntityPlayer entityPlayer=(EntityPlayer)object;
            if(entityPlayer.openContainer instanceof ContainerChest && ((ContainerChest)entityPlayer.openContainer).getLowerChestInventory() == this && isUseableByPlayer(entityPlayer)) data++;
        }
        updateBlockState(data);
    }

    @Override
    public void openInventory() {
        if(worldObj != null && !worldObj.isRemote) updateBlockState(openersCounter + 1);
    }

    @Override
    public void closeInventory() {
        if(worldObj != null && !worldObj.isRemote) updateBlockState(openersCounter - 1);
    }

    public void setCustomName(String string) {
        customName=string == null || string.isEmpty() ? null : string;
        inventoryChanged();
    }

    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? customName : "container.farmersdelight.cabinet";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return customName != null && !customName.isEmpty();
    }

    @Override
    public int getSizeInventory() {
        return INVENTORY_SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int data) {
        return data >= 0 && data < INVENTORY_SIZE ? inventory[data] : null;
    }

    @Override
    public ItemStack decrStackSize(int data,int metadata) {
        if(data < 0 || data >= INVENTORY_SIZE || metadata <= 0 || inventory[data] == null) return null;
        ItemStack itemStack;
        if(inventory[data].stackSize <= metadata) {
            itemStack=inventory[data];
            inventory[data]=null;
        } else {
            itemStack=inventory[data].splitStack(metadata);
            if(inventory[data].stackSize <= 0) inventory[data]=null;
        }
        inventoryChanged();
        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int data) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int data,ItemStack itemStack) {
        if(data < 0 || data >= INVENTORY_SIZE) return;
        inventory[data]=itemStack;
        if(itemStack != null) itemStack.stackSize=Math.min(itemStack.stackSize,Math.min(getInventoryStackLimit(),itemStack.getMaxStackSize()));
        inventoryChanged();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return worldObj != null && worldObj.getTileEntity(xCoord,yCoord,zCoord) == this && entityPlayer.getDistanceSq(xCoord + 0.5,yCoord + 0.5,zCoord + 0.5) <= 64;
    }

    @Override
    public boolean isItemValidForSlot(int data,ItemStack itemStack) {
        return data >= 0 && data < INVENTORY_SIZE && itemStack != null && itemStack.stackSize > 0;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int data) {
        return SLOT.clone();
    }

    @Override
    public boolean canInsertItem(int data,ItemStack itemStack,int metadata) {
        return isItemValidForSlot(data,itemStack);
    }

    @Override
    public boolean canExtractItem(int data,ItemStack itemStack,int metadata) {
        return data >= 0 && data < INVENTORY_SIZE && itemStack != null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        for(int index=0;index < INVENTORY_SIZE;index++) {
            inventory[index]=null;
        }
        NBTTagList nbtTagList=nbtTagCompound.getTagList("Items",10);
        for(int index=0;index < nbtTagList.tagCount();index++) {
            NBTTagCompound tagCompound=nbtTagList.getCompoundTagAt(index);
            int data=tagCompound.getByte("Slot") & 255;
            if(data < 0 || data >= INVENTORY_SIZE) continue;
            ItemStack itemStack=ItemStack.loadItemStackFromNBT(tagCompound);
            if(itemStack != null && itemStack.stackSize > 0) inventory[data]=itemStack;
        }
        customName=nbtTagCompound.hasKey("CustomName",8) ? nbtTagCompound.getString("CustomName") : null;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        NBTTagList nbtTagList=new NBTTagList();
        for(int index=0;index < INVENTORY_SIZE;index++) {
            if(inventory[index] == null || inventory[index].stackSize <= 0) continue;
            NBTTagCompound tagCompound=inventory[index].writeToNBT(new NBTTagCompound());
            tagCompound.setByte("Slot",(byte)index);
            nbtTagList.appendTag(tagCompound);
        }
        nbtTagCompound.setTag("Items",nbtTagList);
        if(customName != null) nbtTagCompound.setString("CustomName",customName);
    }

    public void dropItems() {
        if(worldObj == null || worldObj.isRemote) return;
        for(int index=0;index < INVENTORY_SIZE;index++) {
            ItemStack itemStack=inventory[index];
            if(itemStack == null || itemStack.stackSize <= 0) continue;
            while (itemStack.stackSize > 0) {
                int data=Math.min(itemStack.stackSize,10 + worldObj.rand.nextInt(21));
                ItemStack stack=itemStack.copy();
                stack.stackSize=data;
                itemStack.stackSize -= data;
                EntityItem entityItem=new EntityItem(worldObj,xCoord + 0.15 + worldObj.rand.nextFloat() * 0.7,yCoord + 0.15 + worldObj.rand.nextFloat() * 0.7,zCoord + 0.15 + worldObj.rand.nextFloat() * 0.7,stack);
                entityItem.motionX=worldObj.rand.nextGaussian() * 0.05;
                entityItem.motionY=worldObj.rand.nextGaussian() * 0.05 + 0.2;
                entityItem.motionZ=worldObj.rand.nextGaussian() * 0.05;
                worldObj.spawnEntityInWorld(entityItem);
            }
            inventory[index]=null;
        }
        inventoryChanged();
    }

    private void updateBlockState(int data) {
        data=Math.max(0,data);
        if(worldObj == null || worldObj.isRemote || worldObj.getBlock(xCoord,yCoord,zCoord) != getBlockType()) return;
        int metadata=worldObj.getBlockMetadata(xCoord,yCoord,zCoord);
        boolean isOpen=(metadata & CabinetBlock.OPEN) != 0;
        boolean open=data > 0;
        int index=openersCounter;
        openersCounter=data;
        if(isOpen == open) return;
        worldObj.setBlockMetadataWithNotify(xCoord,yCoord,zCoord,(metadata & CabinetBlock.FACING) | (open ? CabinetBlock.OPEN : 0),3);
        if(index == data) return;
        int values=metadata & CabinetBlock.FACING;
        double[] x={0,1,0,-1};
        double[] z={-1,0,1,0};
        worldObj.playSoundEffect(xCoord + 0.5 + x[index] * 0.5,yCoord + 0.5,zCoord + 0.5 + z[index] * 0.5,open ? "delightlegacy:block.cabinet.open" : "delightlegacy:block.cabinet.close",0.5f,0.9f + worldObj.rand.nextFloat() * 0.1f);
    }
}
