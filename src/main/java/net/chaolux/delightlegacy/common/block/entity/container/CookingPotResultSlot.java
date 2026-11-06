package net.chaolux.delightlegacy.common.block.entity.container;

import net.chaolux.delightlegacy.common.block.entity.CookingPotBlockEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CookingPotResultSlot extends Slot {
    private final CookingPotBlockEntity cookingPot;
    private final EntityPlayer entityPlayer;
    private int removeCount;
    public CookingPotResultSlot(EntityPlayer entityPlayer, CookingPotBlockEntity cookingPot,int index,int x,int y) {
        super(cookingPot,index,x,y);
        this.cookingPot=cookingPot;
        this.entityPlayer=entityPlayer;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int value) {
        if(getHasStack()) removeCount += Math.min(value,getStack().stackSize);
        return super.decrStackSize(value);
    }

    public void onQuickCraft(int value) {
        removeCount += value;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer entityPlayer,ItemStack itemStack) {
        checkTakeAchievements(itemStack);
        super.onPickupFromSlot(entityPlayer,itemStack);
    }

    private void checkTakeAchievements(ItemStack itemStack) {
        if(removeCount <= 0) return;
        itemStack.onCrafting(entityPlayer.worldObj,entityPlayer,removeCount);
        if(!entityPlayer.worldObj.isRemote) cookingPot.awardUsedRecipes(entityPlayer,removeCount);
        removeCount=0;
    }
 }
