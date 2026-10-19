package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.legacy.common.block.LegacyCropBlock;
import net.chaolux.delightlegacy.legacy.common.block.LegacyLiquidBlock;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.chaolux.delightlegacy.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class RiceCropBlock extends LegacyCropBlock implements LegacyLiquidBlock {
    private static final int AGE=3;
    private static final int SUPPORTING=4;
    private boolean restore;
    public RiceCropBlock() {
        super(3);
    }

    @Override
    public int getAgeData(int data) {
        return data & AGE;
    }

    @Override
    public int getAge(int data,int age) {
        return (data & (SUPPORTING | 8)) | (Math.max(0,Math.min(age,3)) & AGE);
    }

    @Override
    protected boolean canPlaceBlockOn(Block block) {
        return block == Blocks.dirt || block == Blocks.grass || block == Blocks.farmland;
    }

    @Override
    public boolean canBlockStay(World world,int x,int y,int z) {
        boolean value=world.getBlockLightValue(x,y,z) >= 8 || world.canBlockSeeTheSky(x,y,z);
        return value && canPlaceBlockOn(world.getBlock(x,y - 1,z));
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if(!canBlockStay(world,x,y,z)) {
            if(!world.isRemote) {
                int data=world.getBlockMetadata(x,y,z);
                dropBlockAsItem(world,x,y,z,data,0);
                restore(world,x,y,z);
            }
            return;
        }
        setSupporting(world,x,y,z);
        if(world.getBlockLightValue(x,y + 1,z) < 9) return;
        int data=world.getBlockMetadata(x,y,z);
        int ageData=getAgeData(data);
        if(ageData < 3) {
            if(random.nextInt(4) == 0) world.setBlockMetadataWithNotify(x,y,z,getAge(data,ageData + 1),2);
            return;
        }
        if(!world.isRemote && world.isAirBlock(x,y + 1,z)) {
            world.setBlock(x,y + 1,z,ModBlocks.RICE_CROP_PANICLES.getValue(),0,3);
            world.setBlockMetadataWithNotify(x,y,z,getSupporting(data,true),2);
        }
    }

    @Override
    public boolean func_149851_a(World world,int x,int y,int z,boolean isClient) {
        int ageData=getAgeData(world.getBlockMetadata(x,y,z));
        if(ageData < 3) return true;
        Block block=world.getBlock(x,y + 1,z);
        return block == Blocks.air || block == ModBlocks.RICE_CROP_PANICLES.getValue();
    }

    @Override
    public void func_149853_b(World world,Random random,int x,int y,int z) {
        int data=world.getBlockMetadata(x,y,z);
        int ageData=getAgeData(data);
        int value=1 + random.nextInt(4);
        int age=ageData + value;
        if(age <= 3) {
            world.setBlockMetadataWithNotify(x,y,z,getAge(data,age),2);
            return;
        }
        Block block=world.getBlock(x,y + 1,z);
        if(block == ModBlocks.RICE_CROP_PANICLES.get()) {
            if(block instanceof IGrowable) ((IGrowable) block).func_149853_b(world,random,x,y + 1,z);
            setSupporting(world,x,y,z);
            return;
        }
        if(block == Blocks.air) {
            int ageValue=Math.min(3,Math.max(0,age - 4));
            world.setBlockMetadataWithNotify(x,y,z,getSupporting(getAge(data,3),true),2);
            world.setBlock(x,y + 1,z,ModBlocks.RICE_CROP_PANICLES.getValue(),ageValue,3);
        }
    }

    @Override
    public void onNeighborBlockChange(World world,int x,int y,int z,Block block) {
        if(!canBlockStay(world,x,y,z)) {
            if(!world.isRemote) {
                int data=world.getBlockMetadata(x,y,z);
                dropBlockAsItem(world,x,y,z,data,0);
                restore(world,x,y,z);
            }
            return;
        }
        setSupporting(world,x,y,z);
    }

    @Override
    public void onBlockDestroyedByPlayer(World world,int x,int y,int z,int data) {
        restore(world,x,y,z);
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        restore(world,x,y,z);
    }

    @Override
    protected Item getSeed() {
        return ModItems.RICE.getValue();
    }

    @Override
    protected Item getCrop() {
        return ModItems.RICE.getValue();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int data,int fortune) {
        ArrayList<ItemStack> arrayList=new ArrayList<ItemStack>();
        arrayList.add(new ItemStack(ModItems.RICE.getValue(),1,0));
        return arrayList;
    }

    @Override
    protected float getStage(int stage) {
        float[] stages={8.0f / 16.0f,10.0f / 16.0f,12.0f / 16.0f,1.0f};
        return stages[Math.max(0,Math.min(stage,3))];
    }

    @Override
    public Material getMaterial() {
        return Material.water;
    }

    @Override
    public Block getLiquid() {
        return Blocks.water;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    private void restore(World world, int x, int y, int z) {
        if(world.isRemote || restore) return;
        restore =true;
        try {
            world.setBlock(x,y,z,Blocks.water,0,3);
        } finally {
            restore =false;
        }
    }

    public boolean isSupporting(int data) {
        return (data & SUPPORTING) != 0;
    }

    public int getSupporting(int data,boolean value) {
        return value ? data | SUPPORTING : data & ~SUPPORTING;
    }

    public void setSupporting(World world,int x,int y,int z) {
        if(world.getBlock(x,y,z) != this) return;
        int data=world.getBlockMetadata(x,y,z);
        boolean value=world.getBlock(x,y + 1,z) == ModBlocks.RICE_CROP_PANICLES.getValue();
        int stage=getSupporting(data,value);
        if(stage != data) world.setBlockMetadataWithNotify(x,y,z,stage,2);
    }
}
