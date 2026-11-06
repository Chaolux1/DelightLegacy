package net.chaolux.delightlegacy.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.List;

public class CookingPotItem extends ItemBlock {
    public CookingPotItem(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list,boolean value) {
        if(itemStack == null || !itemStack.hasTagCompound()) return;
        NBTTagCompound nbtTagCompound=itemStack.getTagCompound();
        if(!nbtTagCompound.hasKey("BlockEntityTag",10)) return;
        NBTTagCompound tagCompound=nbtTagCompound.getCompoundTag("BlockEntityTag");
        if(!tagCompound.hasKey("Meal",10)) return;
        ItemStack stack=ItemStack.loadItemStackFromNBT(tagCompound.getCompoundTag("Meal"));
        if(stack == null || stack.stackSize <= 0) return;
        String string=stack.stackSize == 1 ? "tooltip.delightlegacy.cooking_pot.single_serving" : "tooltip.delightlegacy.cooking_pot.many_servings";
        list.add(stack.stackSize == 1 ? StatCollector.translateToLocal(string) : StatCollector.translateToLocalFormatted(string,stack.stackSize));
        list.add(" " + stack.getDisplayName());
    }
}
