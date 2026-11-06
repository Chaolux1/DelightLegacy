package net.chaolux.delightlegacy.common.block.entity.container;

import net.chaolux.delightlegacy.common.block.entity.CookingPotBlockEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CookingPotMenu extends Container {
    private final CookingPotBlockEntity cookingPotBlockEntity;
    private int cookTime;
    private int cookTimeTotal;
    public CookingPotMenu(InventoryPlayer inventoryPlayer,CookingPotBlockEntity cookingPotBlockEntity) {
        this.cookingPotBlockEntity=cookingPotBlockEntity;
        for(int x=0;x < 2;x++) {
            for (int y=0;y < 3;y++) {
                addSlotToContainer(new Slot(cookingPotBlockEntity,x * 3 + y,30 + y * 18,17 + x * 18));
            }
        }
        addSlotToContainer(new CookingPotMealSlot(cookingPotBlockEntity,CookingPotBlockEntity.MEAL_DISPLAY_SLOT,124,26));
        addSlotToContainer(new Slot(cookingPotBlockEntity,CookingPotBlockEntity.CONTAINER_SLOT,92,55) {
            @Override
            public boolean isItemValid(ItemStack itemStack) {
                return cookingPotBlockEntity.isContainer(itemStack);
            }
        });
        addSlotToContainer(new CookingPotResultSlot(inventoryPlayer.player,cookingPotBlockEntity,CookingPotBlockEntity.OUTPUT_SLOT,124,55));
        for(int x=0;x < 3;x++) {
            for (int y=0;y < 9;y++) {
                addSlotToContainer(new Slot(inventoryPlayer,9 + x * 9 + y,8 + y * 18,84 + x * 18));
            }
        }
        for(int y=0;y < 9;y++) {
            addSlotToContainer(new Slot(inventoryPlayer,y,8 + y * 18,142));
        }
        cookingPotBlockEntity.openInventory();
    }

    public CookingPotBlockEntity getCookingPotBlockEntity() {
        return cookingPotBlockEntity;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return cookingPotBlockEntity.isUseableByPlayer(entityPlayer);
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        cookingPotBlockEntity.closeInventory();
    }

    @Override
    public void addCraftingToCrafters(ICrafting iCrafting) {
        super.addCraftingToCrafters(iCrafting);
        iCrafting.sendProgressBarUpdate(this,0,cookingPotBlockEntity.getCookTime());
        iCrafting.sendProgressBarUpdate(this,1,cookingPotBlockEntity.getCookTimeTotal());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for(Object object : crafters) {
            ICrafting iCrafting=(ICrafting)object;
            if(cookTime != cookingPotBlockEntity.getCookTime()) iCrafting.sendProgressBarUpdate(this,0,cookingPotBlockEntity.getCookTime());
            if(cookTimeTotal != cookingPotBlockEntity.getCookTimeTotal()) iCrafting.sendProgressBarUpdate(this,1,cookingPotBlockEntity.getCookTimeTotal());
        }
        cookTime=cookingPotBlockEntity.getCookTime();
        cookTimeTotal=cookingPotBlockEntity.getCookTimeTotal();
    }

    @Override
    public void updateProgressBar(int value,int data) {
        if(value == 0) cookTime=data;
        if(value == 1) cookTimeTotal=data;
    }

    public int getCookTime(int value) {
        if(cookTimeTotal <= 0 || cookTime <= 0) return 0;
        return Math.min(value,cookTime * value / cookTimeTotal);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer,int value) {
        if(value < 0 || value >= inventorySlots.size()) return null;
        if(value == CookingPotBlockEntity.MEAL_DISPLAY_SLOT) return null;
        Slot slot=(Slot)inventorySlots.get(value);
        if(slot == null || !slot.getHasStack()) return null;
        ItemStack itemStack=slot.getStack();
        ItemStack stack=itemStack.copy();
        if(value == CookingPotBlockEntity.OUTPUT_SLOT) {
            if(!mergeItemStack(itemStack,9,45,true)) return null;
            int data=stack.stackSize - itemStack.stackSize;
            if(slot instanceof CookingPotResultSlot) ((CookingPotResultSlot)slot).onQuickCraft(data);
        } else if(value >= 9) {
            if(cookingPotBlockEntity.isContainer(itemStack)) {
                if(!mergeItemStack(itemStack,CookingPotBlockEntity.CONTAINER_SLOT,CookingPotBlockEntity.CONTAINER_SLOT + 1,false)) return null;
            } else {
                if(!mergeItemStack(itemStack,0,6,false)) return null;
            }
        } else {
            if(!mergeItemStack(itemStack,9,45,false)) return null;
        }
        if(itemStack.stackSize <= 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }
        if(itemStack.stackSize == stack.stackSize) return null;
        slot.onPickupFromSlot(entityPlayer,stack);
        return stack;
    }
}
