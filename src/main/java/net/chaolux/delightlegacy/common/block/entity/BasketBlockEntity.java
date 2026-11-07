package net.chaolux.delightlegacy.common.block.entity;

import net.chaolux.delightlegacy.common.block.BasketBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class BasketBlockEntity extends SyncedBlockEntity implements ISidedInventory {
    private static final int INVENTORY_SIZE=27;
    private static final int[] SLOT=new int[INVENTORY_SIZE];
    private final ItemStack[] inventory=new ItemStack[INVENTORY_SIZE];
    private String customName;
    private int transferCooldown;
    static {
        for(int index=0;index < SLOT.length;index++) {
            SLOT[index]=index;
        }
    }

    @Override
    public void updateEntity() {
        if(worldObj == null || worldObj.isRemote || worldObj.getBlock(xCoord,yCoord,zCoord) != getBlockType()) return;
        if(transferCooldown > 0) {
            transferCooldown--;
            return;
        }
        if(worldObj.isBlockIndirectlyGettingPowered(xCoord,yCoord,zCoord) || isFull()) return;
        if(pullItems()) {
            transferCooldown=8;
            inventoryChanged();
        }
    }

    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? customName : "container.farmersdelight.basket";
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
    public ItemStack getStackInSlot(int value) {
        return value >= 0 && value < INVENTORY_SIZE ? inventory[value] : null;
    }

    @Override
    public ItemStack decrStackSize(int value,int data) {
        if(value < 0 || value >= INVENTORY_SIZE || data <= 0 || inventory[value] == null) return null;
        ItemStack itemStack;
        if(inventory[value].stackSize <= data) {
            itemStack=inventory[value];
            inventory[value]=null;
        } else {
            itemStack=inventory[value].splitStack(data);
            if(inventory[value].stackSize <= 0) inventory[value]=null;
        }
        inventoryChanged();
        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int value) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int value,ItemStack itemStack) {
        if(value < 0 || value >= INVENTORY_SIZE) return;
        inventory[value]=itemStack;
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
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int value,ItemStack itemStack) {
        return value >= 0 && value < INVENTORY_SIZE && itemStack != null && itemStack.stackSize > 0;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int value) {
        return SLOT.clone();
    }

    @Override
    public boolean canInsertItem(int value,ItemStack itemStack,int data) {
        return isItemValidForSlot(value,itemStack);
    }

    @Override
    public boolean canExtractItem(int value,ItemStack itemStack,int data) {
        return value >= 0 && value < INVENTORY_SIZE && itemStack != null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        for (int index=0;index < INVENTORY_SIZE;index++) {
            inventory[index]=null;
        }
        NBTTagList nbtTagList=nbtTagCompound.getTagList("Items",10);
        for(int index=0;index < nbtTagList.tagCount();index++) {
            NBTTagCompound tagCompound=nbtTagList.getCompoundTagAt(index);
            int value=tagCompound.getByte("Slot") & 255;
            if(value >= INVENTORY_SIZE) continue;
            ItemStack itemStack=ItemStack.loadItemStackFromNBT(tagCompound);
            if(itemStack != null && itemStack.stackSize > 0) inventory[value]=itemStack;
        }
        transferCooldown=Math.max(0,nbtTagCompound.getInteger("TransferCooldown"));
        customName=nbtTagCompound.hasKey("CustomName",8) ? nbtTagCompound.getString("CustomName") : null;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        NBTTagList nbtTagList=new NBTTagList();
        for(int index=0;index < INVENTORY_SIZE;index++) {
            ItemStack itemStack=inventory[index];
            if(itemStack == null || itemStack.stackSize <= 0) continue;
            NBTTagCompound tagCompound=itemStack.writeToNBT(new NBTTagCompound());
            tagCompound.setByte("Slot",(byte)index);
            nbtTagList.appendTag(tagCompound);
        }
        nbtTagCompound.setTag("Items",nbtTagList);
        nbtTagCompound.setInteger("TransferCooldown",transferCooldown);
        if(customName != null) nbtTagCompound.setString("CustomName",customName);
    }

    public void dropItem() {
        if(worldObj == null || worldObj.isRemote) return;
        for(int index=0;index < INVENTORY_SIZE;index++) {
            ItemStack itemStack=inventory[index];
            if(itemStack == null || itemStack.stackSize <= 0) continue;
            while (itemStack.stackSize > 0) {
                int value=Math.min(itemStack.stackSize,10 + worldObj.rand.nextInt(21));
                ItemStack stack=itemStack.copy();
                stack.stackSize=value;
                itemStack.stackSize -=value;
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

    public void onEntityCollision(EntityItem entityItem) {
        if(worldObj == null || worldObj.isRemote || entityItem == null || entityItem.isDead || transferCooldown > 0) return;
        if(entityItem.getEntityItem() == null || entityItem.getEntityItem().stackSize <= 0) return;
        if(worldObj.isBlockIndirectlyGettingPowered(xCoord,yCoord,zCoord) || isFull()) return;
        if(!getFacingCollectionArea(worldObj.getBlockMetadata(xCoord,yCoord,zCoord)).intersectsWith(entityItem.boundingBox)) return;
        if(captureItem(entityItem)) {
            transferCooldown=8;
            inventoryChanged();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean pullItems() {
        int value=worldObj.getBlockMetadata(xCoord,yCoord,zCoord);
        AxisAlignedBB axisAlignedBB=getFacingCollectionArea(value);
        List<EntityItem> entityItemList=worldObj.getEntitiesWithinAABB(EntityItem.class,axisAlignedBB);
        for(EntityItem entityItem : entityItemList) {
            if(entityItem.isDead || entityItem.getEntityItem() == null || entityItem.getEntityItem().stackSize <= 0) continue;
            if(captureItem(entityItem)) return true;
        }
        return false;
    }

    public AxisAlignedBB getFacingCollectionArea(int value) {
        double minX=xCoord;
        double minY=yCoord;
        double minZ=zCoord;
        double maxX=xCoord + 1;
        double maxY=yCoord + 1;
        double maxZ=zCoord + 1;
        if(value == BasketBlock.DOWN) minY--;
        if(value == BasketBlock.UP) maxY++;
        if(value == BasketBlock.NORTH) minZ--;
        if(value == BasketBlock.SOUTH) maxZ++;
        if(value == BasketBlock.WEST) minX--;
        if(value == BasketBlock.EAST) maxX++;
        return AxisAlignedBB.getBoundingBox(minX,minY,minZ,maxX,maxY,maxZ);
    }

    private boolean captureItem(EntityItem entityItem) {
        ItemStack itemStack=entityItem.getEntityItem();
        ItemStack stack=itemStack.copy();
        for(int index=0;index < INVENTORY_SIZE && stack.stackSize > 0;index++) {
            ItemStack item=inventory[index];
            if(item == null || !item.isItemEqual(stack) || !ItemStack.areItemStackTagsEqual(item,stack)) continue;
            int value=Math.min(getInventoryStackLimit(),item.getMaxStackSize());
            int data=Math.min(stack.stackSize,value - item.stackSize);
            if(data <= 0) continue;
            item.stackSize += data;
            stack.stackSize -= data;
        }
        for (int index=0;index < INVENTORY_SIZE && stack.stackSize > 0;index++) {
            if(inventory[index] != null) continue;
            int value=Math.min(stack.stackSize,Math.min(getInventoryStackLimit(),stack.getMaxStackSize()));
            if(value <= 0) break;
            ItemStack item=stack.copy();
            item.stackSize=value;
            inventory[index]=item;
            stack.stackSize -= value;
        }
        if(stack.stackSize == itemStack.stackSize) return false;
        if(stack.stackSize <= 0) {
            entityItem.setDead();
        } else {
            entityItem.setEntityItemStack(stack);
        }
        return true;
    }

    private boolean isFull() {
        for(ItemStack itemStack : inventory) {
            if(itemStack == null || itemStack.stackSize < Math.min(getInventoryStackLimit(),itemStack.getMaxStackSize())) return false;
        }
        return true;
    }

    public void setCustomName(String string) {
        customName=string == null || string.isEmpty() ? null : string;
        inventoryChanged();
    }
}
