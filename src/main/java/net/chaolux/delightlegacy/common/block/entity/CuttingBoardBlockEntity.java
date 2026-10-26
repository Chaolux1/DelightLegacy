package net.chaolux.delightlegacy.common.block.entity;

import net.chaolux.delightlegacy.common.crafting.CuttingBoardRecipe;
import net.chaolux.delightlegacy.common.crafting.CuttingBoardRecipeWrapper;
import net.chaolux.delightlegacy.common.utility.ItemUtils;
import net.chaolux.delightlegacy.legacy.common.item.LegacyToolAction;
import net.chaolux.delightlegacy.legacy.data.recipe.LegacyRecipeManager;
import net.chaolux.delightlegacy.registry.ModRecipeTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class CuttingBoardBlockEntity extends TileEntity implements ISidedInventory {
    private static final int[] SLOT={0};
    private ItemStack itemStack;
    private boolean value;
    private ResourceLocation resourceLocation;
    public ItemStack getStoredItem() {
        return itemStack == null ? null : itemStack.copy();
    }

    public boolean isEmpty() {
        return itemStack == null || itemStack.stackSize <= 0;
    }

    public boolean isItemCarvingBoard() {
        return value;
    }

    public boolean canAddItem(ItemStack stack) {
        if(value || stack == null || stack.stackSize <= 0) return false;
        if(isEmpty()) return true;
        return itemStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(itemStack,stack) && itemStack.stackSize < Math.min(getInventoryStackLimit(),itemStack.getMaxStackSize());
    }

    public ItemStack addItem(ItemStack stack) {
        if(!canAddItem(stack)) return stack == null ? null : stack.copy();
        int data=isEmpty() ? Math.min(64,stack.getMaxStackSize()) : Math.min(64,itemStack.getMaxStackSize());
        int metadata=isEmpty() ? data : data - itemStack.stackSize;
        int dataValue=Math.min(metadata,stack.stackSize);
        if(dataValue <= 0) return stack.copy();
        if(isEmpty()) {
            itemStack=stack.copy();
            itemStack.stackSize=dataValue;
        } else {
            itemStack.stackSize += dataValue;
        }
        ItemStack item=stack.copy();
        item.stackSize -= dataValue;
        resourceLocation=null;
        set();
        return item.stackSize > 0 ? item : null;
    }

    public ItemStack removeItem() {
        if(isEmpty()) return null;
        ItemStack stack=itemStack;
        itemStack=null;
        value=false;
        resourceLocation=null;
        set();
        return stack;
    }

    public boolean isCarveTool(ItemStack stack) {
        if(!isEmpty() || stack == null || stack.stackSize != 1) return false;
        Item item=stack.getItem();
        return item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemHoe || item instanceof ItemShears || item instanceof LegacyToolAction || ItemUtils.isKnife(stack);
    }

    public boolean carveToolOnBoard(ItemStack stack) {
        if(!isCarveTool(stack)) return false;
        itemStack=stack.copy();
        value=true;
        resourceLocation=null;
        set();
        return true;
    }

    public boolean processStoredItemUsingTool(ItemStack stack, EntityPlayer entityPlayer) {
        if(worldObj == null || worldObj.isRemote || value || isEmpty() || stack == null) return false;
        CuttingBoardRecipeWrapper cuttingBoardRecipeWrapper=new CuttingBoardRecipeWrapper(itemStack,stack);
        CuttingBoardRecipe cuttingBoardRecipe=null;
        if(resourceLocation != null) {
            Optional<CuttingBoardRecipe> cuttingBoardRecipeOptional= LegacyRecipeManager.getInstance().getRecipe(ModRecipeTypes.CUTTING,resourceLocation);
            if(cuttingBoardRecipeOptional.isPresent() && cuttingBoardRecipeOptional.get().matches(cuttingBoardRecipeWrapper)) cuttingBoardRecipe=cuttingBoardRecipeOptional.get();
        }
        if(cuttingBoardRecipe == null) {
            Optional<CuttingBoardRecipe> cuttingBoardRecipeOptional=LegacyRecipeManager.getInstance().getRecipeT(ModRecipeTypes.CUTTING,cuttingBoardRecipeWrapper);
            if(!cuttingBoardRecipeOptional.isPresent()) return false;
            cuttingBoardRecipe=cuttingBoardRecipeOptional.get();
            resourceLocation=cuttingBoardRecipe.getId();
        }
        ItemStack item=itemStack.copy();
        int data= EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId,stack);
        List<ItemStack> itemStackList=cuttingBoardRecipe.rollResult(worldObj.rand,data * 0.1f);
        int metadata=getBlockMetadata() & 3;
        double x=metadata == 0 ? -0.2 : metadata == 2 ? 0.2 : 0;
        double z=metadata == 1 ? -0.2 : metadata == 3 ? 0.2 : 0;
        for (ItemStack stacks : itemStackList) {
            if(stacks == null || stacks.stackSize <= 0) continue;
            EntityItem entityItem=new EntityItem(worldObj,xCoord + 0.5 + x,yCoord + 0.2,zCoord + 0.5 + z,stacks.copy());
            entityItem.motionX=x;
            entityItem.motionY=0;
            entityItem.motionZ=z;
            entityItem.delayBeforeCanPickup=10;
            worldObj.spawnEntityInWorld(entityItem);
        }
        String string=cuttingBoardRecipe.getSoundEventID();
        if(string == null || string.isEmpty()) {
            if (stack.getItem() instanceof ItemShears) {
                string = "mob.sheep.shear";
            } else if (ItemUtils.isCut(stack)) {
                string = "delightlegacy:block.cutting_board.knife_cut";
            } else {
                string = "dig.wood";
            }
        } else if("minecraft:item.axe.strip".equals(string)) {
            string="dig.wood";
        } else if("minecraft:block.wool.break".equals(string)) {
            string="dig.cloth";
        } else if (string.startsWith("farmersdelight:")) {
            string="delightlegacy:" + string.substring("farmersdelight:".length());
        }
        worldObj.playSoundEffect(xCoord + 0.5,yCoord + 0.5,zCoord + 0.5,string,0.8f,1.0f);
        decrStackSize(0,1);
        if(entityPlayer != null && !entityPlayer.capabilities.isCreativeMode) {
            stack.damageItem(1,entityPlayer);
            if(stack.stackSize <= 0) entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem,null);
            entityPlayer.inventory.markDirty();
        }
        return true;
    }

    public void dropItemStack() {
        if(worldObj == null || worldObj.isRemote) return;
        ItemStack stack=removeItem();
        if(stack == null) return;
        EntityItem entityItem=new EntityItem(worldObj,xCoord + 0.5,yCoord + 0.2,zCoord + 0.5,stack);
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

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int value) {
        return value == 0 ? itemStack : null;
    }

    @Override
    public ItemStack decrStackSize(int data,int metadata) {
        if(data != 0 || isEmpty() || metadata <= 0) return null;
        if(metadata >= itemStack.stackSize) return removeItem();
        ItemStack stack=itemStack.splitStack(metadata);
        resourceLocation=null;
        set();
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int value) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int data,ItemStack stack) {
        if (data != 0) return;
        itemStack=stack == null || stack.stackSize <= 0 ? null : stack.copy();
        if(itemStack != null) itemStack.stackSize=Math.min(itemStack.stackSize,Math.min(64,itemStack.getMaxStackSize()));
        value=false;
        resourceLocation=null;
        set();
    }

    @Override
    public String getInventoryName() {
        return "container.delightlegacy.cutting_board";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
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
    public boolean isItemValidForSlot(int data,ItemStack stack) {
        return data == 0 && canAddItem(stack);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int data) {
        return SLOT.clone();
    }

    @Override
    public boolean canInsertItem(int data,ItemStack stack,int metadata) {
        return data == 0 && canAddItem(stack);
    }

    @Override
    public boolean canExtractItem(int data,ItemStack stack,int metadata) {
        return data == 0 && !isEmpty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        value=nbtTagCompound.getBoolean("ItemValue");
        itemStack=nbtTagCompound.hasKey("ItemStack",10) ? ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag("ItemStack")) : null;
        if(isEmpty()) {
            itemStack=null;
            value=false;
        }
        resourceLocation=null;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("ItemValue",value);
        if(!isEmpty()) nbtTagCompound.setTag("ItemStack",itemStack.writeToNBT(new NBTTagCompound()));
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




}
