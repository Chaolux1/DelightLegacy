package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlock;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import java.util.Random;

public class OrganicCompostBlock extends LegacyBlock {
    public static final int COMPOSTING=7;
    public OrganicCompostBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if(world.isRemote) return;
        float chance = 0.0F;
        boolean hasWater = false;
        int maxLight = 0;
        for(int indexX=-1;indexX <= 1;indexX++) {
            for (int indexY=-1;indexY <= 1;indexY++) {
                for (int indexZ=-1;indexZ <= 1;indexZ++) {
                    int blockX=x + indexX;
                    int blockY=y + indexY;
                    int blockZ=z + indexZ;
                    if(isComposting(world,blockX,blockY,blockZ)) chance += 0.02f;
                    Block block=world.getBlock(blockX,blockY,blockZ);
                    if(block.getMaterial() == Material.water) hasWater=true;
                    int value=world.getSavedLightValue(EnumSkyBlock.Sky,blockX,blockY + 1,blockZ);
                    if(value > maxLight) maxLight=value;
                }
            }
        }
        chance += maxLight > 12 ? 0.1f : 0.05f;
        if(hasWater) chance += 0.1f;
        if(random.nextFloat() > chance) return;
        int compostValue=getComposting(world.getBlockMetadata(x,y,z));
        if(compostValue >= COMPOSTING) {
            world.setBlock(x,y,z, ModBlocks.RICH_SOIL.get(),0,3);
            return;
        }
        world.setBlockMetadataWithNotify(x,y,z,compostValue + 1,3);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world,int x,int y,int z,int value) {
        return COMPOSTING + 1 - getComposting(world.getBlockMetadata(x,y,z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world,int x,int y,int z,Random random) {
        super.randomDisplayTick(world,x,y,z,random);
        if(random.nextInt(10) == 0) world.spawnParticle("townaura",x + random.nextFloat(),y + 1.1,z + random.nextFloat(),0,0,0);
    }

    @Override
    public int getRenderType() {
        return LegacyModelRenderID.getModelRenderID();
    }

    private boolean isComposting(World world,int x,int y,int z) {
        Block block=world.getBlock(x,y,z);
        if(block == Blocks.brown_mushroom || block == Blocks.red_mushroom || block == Blocks.mycelium) return true;
        if(block == Blocks.dirt && world.getBlockMetadata(x,y,z) == 2) return true;
        return block == ModBlocks.ORGANIC_COMPOST.get() || block == ModBlocks.RICH_SOIL.get() || block == ModBlocks.RICH_SOIL_FARMLAND.get() || block == ModBlocks.BROWN_MUSHROOM_COLONY.get() || block == ModBlocks.RED_MUSHROOM_COLONY.get();
    }

    public int getComposting(int value) {
        return Math.max(0,Math.min(value,COMPOSTING));
    }
}
