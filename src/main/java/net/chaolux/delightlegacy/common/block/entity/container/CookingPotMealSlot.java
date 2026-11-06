package net.chaolux.delightlegacy.common.block.entity.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CookingPotMealSlot extends Slot {
    public CookingPotMealSlot(IInventory iInventory,int x,int y,int z) {
        super(iInventory,x,y,z);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer entityPlayer) {
        return false;
    }
}
