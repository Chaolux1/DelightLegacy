package net.chaolux.delightlegacy.legacy.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class LegacySeedFoodItem extends LegacyItem implements IPlantable {
    private final Block block;
    private final Block soil;
    public LegacySeedFoodItem(Block block,Block soil,LegacyItemProperties legacyItemProperties) {
        super(legacyItemProperties);
        this.block=block;
        this.soil=soil;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world,int x,int y,int z,int value,float mouseX,float mouseY,float mouseZ) {
        if(value != 1) return false;
        if(!entityPlayer.canPlayerEdit(x,y,z,value,itemStack)) return false;
        if(!entityPlayer.canPlayerEdit(x,y + 1,z,value,itemStack)) return false;
        if(!world.isAirBlock(x,y + 1,z)) return false;
        Block farmland=world.getBlock(x,y,z);
        if(farmland != soil && !farmland.canSustainPlant(world,x,y,z, ForgeDirection.UP,this)) return false;
        if(!world.isRemote) {
            world.setBlock(x,y + 1,z,block,0,3);
            if(!entityPlayer.capabilities.isCreativeMode) itemStack.stackSize--;
        }
        return true;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess iBlockAccess,int x,int y,int z) {
        return EnumPlantType.Crop;
    }

    @Override
    public Block getPlant(IBlockAccess iBlockAccess,int x,int y,int z) {
        return block;
    }

    @Override
    public int getPlantMetadata(IBlockAccess iBlockAccess,int x,int y,int z) {
        return 0;
    }
}
