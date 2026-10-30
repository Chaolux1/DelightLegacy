package net.chaolux.delightlegacy.common.block.entity;

import net.chaolux.delightlegacy.common.block.AbstractStoveBlock;
import net.chaolux.delightlegacy.legacy.data.recipe.CampfireCookingRecipe;
import net.chaolux.delightlegacy.legacy.data.recipe.LegacyRecipeManager;
import net.chaolux.delightlegacy.registry.ModRecipeTypes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractStoveBlockEntity extends TileEntity {
    private final ItemStack[] itemStack;
    private final int[] cookingProgress;
    private final int[] cookingTime;
    protected AbstractStoveBlockEntity() {
        itemStack=new ItemStack[getInventorySlotCount()];
        cookingProgress=new int[itemStack.length];
        cookingTime=new int[itemStack.length];
    }

    protected abstract int getInventorySlotCount();

    @Override
    public void updateEntity() {
        if(worldObj == null || isEmpty()) return;
        if(worldObj.isRemote) {
            if(AbstractStoveBlock.lit(getBlockMetadata())) show();
            return;
        }
        if(AbstractStoveBlock.isStoveTopCovered(worldObj,xCoord,yCoord,zCoord)) {
            drop();
            return;
        }
        boolean value=AbstractStoveBlock.lit(getBlockMetadata());
        boolean data=false;
        for(int index=0;index < itemStack.length;index++) {
            if(itemStack[index] == null) continue;
            if(value) {
                cookingProgress[index]++;
                data=true;
                if(cookingProgress[index] < cookingTime[index]) continue;
                CampfireCookingRecipe campfireCookingRecipe=getResult(itemStack[index]);
                ItemStack stack=campfireCookingRecipe == null ? itemStack[index].copy() : campfireCookingRecipe.getResult();
                dropItem(stack,0.1f);
                itemStack[index]=null;
                cookingProgress[index]=0;
                cookingTime[index]=0;
                set();
            } else if(cookingProgress[index] > 0) {
                cookingProgress[index]=Math.max(0,cookingProgress[index] - 2);
                data=true;
            }
        }
        if(data) markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        for(int index=0;index < itemStack.length;index++) {
            itemStack[index]=null;
        }
        NBTTagList nbtTagList=nbtTagCompound.getTagList("Inventory",10);
        for (int data=0;data < nbtTagList.tagCount();data++) {
            NBTTagCompound tagCompound=nbtTagList.getCompoundTagAt(data);
            int metadata=tagCompound.getByte("Metadata") & 255;
            if(metadata >= itemStack.length) continue;
            ItemStack stack=ItemStack.loadItemStackFromNBT(tagCompound);
            if(stack != null && stack.stackSize > 0) {
                stack.stackSize=1;
                itemStack[metadata]=stack;
            }
        }
        int[] times=nbtTagCompound.getIntArray("CookingTime");
        int[] totalTimes=nbtTagCompound.getIntArray("CookingTotalTimes");
        for(int index=0;index < itemStack.length;index++) {
            cookingProgress[index]=index < times.length ? Math.max(0,times[index]) : 0;
            cookingTime[index]=index < totalTimes.length ? Math.max(1,totalTimes[index]) : 600;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        NBTTagList nbtTagList=new NBTTagList();
        for (int index=0;index < itemStack.length;index++) {
            if(itemStack[index] == null) continue;
            NBTTagCompound tagCompound=itemStack[index].writeToNBT(new NBTTagCompound());
            tagCompound.setByte("Metadata",(byte)index);
            nbtTagList.appendTag(tagCompound);
        }
        nbtTagCompound.setTag("Inventory",nbtTagList);
        nbtTagCompound.setIntArray("CookingTime",cookingProgress);
        nbtTagCompound.setIntArray("CookingTotalTimes",cookingTime);
    }

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

    public int getItems() {
        return itemStack.length;
    }

    public ItemStack getStackInSlot(int value) {
        return value < 0 || value >= itemStack.length || itemStack[value] == null ? null : itemStack[value].copy();
    }

    public boolean isEmpty() {
        for(ItemStack stack : itemStack) {
            if(stack != null && stack.stackSize > 0) return false;
        }
        return true;
    }

    public int getNextEmptySlot() {
        for(int index=0;index < itemStack.length;index++) {
            if(itemStack[index] == null) return index;
        }
        return -1;
    }

    public boolean getItem(ItemStack stack) {
        return stack != null && stack.stackSize > 0 && getNextEmptySlot() >= 0 && getResult(stack) != null;
    }

    public boolean placeFood(ItemStack stack,boolean value) {
        if(worldObj == null || worldObj.isRemote || stack == null || stack.stackSize <= 0) return false;
        int data=getNextEmptySlot();
        if(data < 0) return false;
        CampfireCookingRecipe campfireCookingRecipe=getResult(stack);
        if(campfireCookingRecipe == null) return false;
        itemStack[data]=stack.copy();
        itemStack[data].stackSize=1;
        cookingTime[data]=campfireCookingRecipe.getCookingTime();
        cookingProgress[data]=0;
        if(!value) stack.stackSize--;
        set();
        return true;
    }

    private void show() {
        for(int index=0;index < itemStack.length;index++) {
            if(itemStack[index] == null || worldObj.rand.nextFloat() >= 0.2f) continue;
            float[] value=getValue(index);
            int data=AbstractStoveBlock.getFacing(getBlockMetadata());
            double x=data == 1 ? value[1] : data == 2 ? -value[0] : data == 3 ? -value[1] : value[0];
            double z=data == 1 ? -value[0] : data == 2 ? -value[1] : data == 3 ? value[0] : value[1];
            for (int y=0;y < 3;y++) {
                worldObj.spawnParticle("smoke",xCoord + 0.5 + x,yCoord + 1.02,zCoord + 0.5 + z,0,0.0005,0);
            }
        }
    }

    public float[] getValue(int value) {
        return new float[] {(1 - value % 3) * 0.3f,(value / 3 == 0 ? 0.2f : -0.2f)};
    }

    public void drop() {
        if(worldObj == null || worldObj.isRemote) return;
        boolean value=false;
        for(int index=0;index < itemStack.length;index++) {
            if(itemStack[index] == null) continue;
            dropItem(itemStack[index].copy(),0.08f);
            itemStack[index]=null;
            cookingProgress[index]=0;
            cookingTime[index]=0;
            value=true;
        }
        if(value) set();
    }

    private void dropItem(ItemStack stack,float value) {
        if(stack == null || stack.stackSize <= 0) return;
        EntityItem entityItem=new EntityItem(worldObj,xCoord + 0.5,yCoord + 1,zCoord + 0.5,stack);
        entityItem.motionX=worldObj.rand.nextGaussian() * 0.01;
        entityItem.motionY=value;
        entityItem.motionZ=worldObj.rand.nextGaussian() * 0.01;
        entityItem.delayBeforeCanPickup=10;
        worldObj.spawnEntityInWorld(entityItem);
    }

    private void set() {
        markDirty();
        if(worldObj != null) {
            worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
            worldObj.func_147453_f(xCoord,yCoord,zCoord,getBlockType());
        }
    }

    private CampfireCookingRecipe getResult(ItemStack stack) {
        return LegacyRecipeManager.getInstance().getRecipeT(ModRecipeTypes.CAMPFIRE_COOKING,stack).orElse(null);
    }
}
