package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.legacy.common.block.LegacyCropBlock;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.chaolux.delightlegacy.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class RicePaniclesBlock extends LegacyCropBlock {
    public RicePaniclesBlock() {
        super(3);
    }

    @Override
    protected boolean canPlaceBlockOn(Block block) {
        return block == ModBlocks.RICE_CROP.getValue();
    }

    @Override
    public boolean canBlockStay(World world,int x,int y,int z) {
        boolean value=world.getBlockLightValue(x,y,z) >= 8 || world.canBlockSeeTheSky(x,y,z);
        return value && world.getBlock(x,y - 1,z) == ModBlocks.RICE_CROP.getValue();
    }

    @Override
    protected int getBonemealAgeIncrease(Random random) {
        return MathHelper.getRandomIntegerInRange(random,2,5) / 3;
    }

    @Override
    protected Item getSeed() {
        return ModItems.RICE.getValue();
    }

    @Override
    protected Item getCrop() {
        return ModItems.RICE_PANICLE.getValue();
    }

    @Override
    public Item getItem(World world,int x,int y,int z) {
        return ModItems.RICE.getValue();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int data,int fortune) {
        ArrayList<ItemStack> arrayList=new ArrayList<ItemStack>();
        if(isMaxAge(data)) arrayList.add(new ItemStack(ModItems.RICE_PANICLE.getValue(),1,0));
        return arrayList;
    }

    @Override
    public void onNeighborBlockChange(World world,int x,int y,int z,Block block) {
        if(!canBlockStay(world,x,y,z)) {
            if(!world.isRemote) {
                int data=world.getBlockMetadata(x,y,z);
                dropBlockAsItem(world,x,y,z,data,0);
                world.setBlockToAir(x,y,z);
                setSupporting(world,x,y,z);
            }
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World world,int x,int y,int z,int data) {
        setSupporting(world,x,y,z);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        setSupporting(world,x,y,z);
    }

    @Override
    protected float getStage(int stage) {
        float[] stages={8.0f / 16.0f,10.0f / 16.0f,12.0f / 16.0f,1.0f};
        return stages[Math.max(0,Math.min(stage,3))];
    }

    private void setSupporting(World world,int x,int y,int z) {
        if(world.isRemote) return;
        Block block=ModBlocks.RICE_CROP.getValue();
        if(world.getBlock(x,y - 1,z) != block) return;
        ((RiceCropBlock) block).setSupporting(world,x,y - 1,z);
    }
}
