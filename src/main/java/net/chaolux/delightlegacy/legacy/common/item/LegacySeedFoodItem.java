package net.chaolux.delightlegacy.legacy.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class LegacySeedFoodItem extends LegacyItem {
    private final Block block;
    private final Block soil;
    public LegacySeedFoodItem(Block block,Block soil,LegacyItemProperties legacyItemProperties) {
        super(legacyItemProperties);
        this.block=block;
        this.soil=soil;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world,int x,int y,int z,int value,float mouseX,float mouseY,float mouseZ) {
        if(value != 1 || world.getBlock(x,y,z) != soil || !world.isAirBlock(x,y + 1,z)) return false;
        if(!entityPlayer.canPlayerEdit(x,y,z,value,itemStack) || !entityPlayer.canPlayerEdit(x,y + 1,z,value,itemStack)) return false;
        if(!world.isRemote) {
            world.setBlock(x,y + 1,z,block,0,3);
            if(!entityPlayer.capabilities.isCreativeMode) itemStack.stackSize--;
        }
        return true;
    }
}
