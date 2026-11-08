package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Supplier;

public class GleamingSaladBlock extends FeastBlock {
    public GleamingSaladBlock(LegacyBlockProperties legacyBlockProperties, Supplier<Item> servingItem,boolean hasLeftovers) {
        super(legacyBlockProperties,servingItem,hasLeftovers);
    }

    @Override
    public int getLightValue(IBlockAccess iBlockAccess,int x,int y,int z) {
        return Math.min(15,getServings(iBlockAccess,x,y,z) * 3);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess,int x,int y,int z) {
        setBlockBounds(2.0f / 16.0f,0.0f,2.0f / 16.0f,14.0f / 16.0f,6.0f / 16.0f,14.0f / 16.0f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        int value=getServings(world,x,y,z);
        if(value > 0 && random.nextFloat() < 0.05f * value) world.spawnParticle("enchantmenttable",x + 0.5 + random.nextDouble() * 0.5 - 0.25,y + 0.5,z + 0.5 + random.nextDouble() * 0.5 - 0.25,0,0,0);
    }
}
