package net.chaolux.delightlegacy.common.item;

import net.chaolux.delightlegacy.common.block.RiceCropBlock;
import net.chaolux.delightlegacy.legacy.common.item.LegacyItem;
import net.chaolux.delightlegacy.legacy.common.item.LegacyItemProperties;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class RiceItem extends LegacyItem {
    private final Block block;
    public RiceItem(Block block, LegacyItemProperties legacyItemProperties) {
        super(legacyItemProperties);
        this.block=block;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world,int x,int y,int z,int value,float mouseX,float mouseY,float mouseZ) {
        if(value != 1 || !isSoil(world.getBlock(x,y,z))) return false;
        if(!entityPlayer.canPlayerEdit(x,y,z,value,itemStack) || !entityPlayer.canPlayerEdit(x,y + 1,z,value,itemStack)) return false;
        Block blocks=world.getBlock(x,y + 1,z);
        boolean water=blocks == Blocks.water && world.getBlockMetadata(x,y + 1,z) == 0;
        if(!water) {
            if (world.isRemote) entityPlayer.addChatMessage(new ChatComponentTranslation("block.delightlegacy.rice.invalid_placement"));
            return false;
        }
        if(!world.isRemote) {
            world.setBlock(x,y + 1,z,block, 8,3);
            if(!entityPlayer.capabilities.isCreativeMode) itemStack.stackSize--;
        }
        return true;
    }

    private boolean isSoil(Block block) {
        return block == Blocks.dirt || block == Blocks.grass || block == Blocks.farmland;
    }
}
