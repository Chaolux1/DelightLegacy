package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.legacy.common.block.LegacyCropBlock;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.chaolux.delightlegacy.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class BuddingTomatoBlock extends LegacyCropBlock {
    private static final float[] STAGE= {2.0f / 16.0f,6.0f / 16.0f,10.0f / 16.0f,14.0f / 16.0f};
    public BuddingTomatoBlock() {
        super(3);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if(!canBlockStay(world,x,y,z)) {
            if(!world.isRemote) {
                dropBlockAsItem(world,x,y,z,world.getBlockMetadata(x,y,z),0);
                world.setBlockToAir(x,y,z);
            }
            return;
        }
        if(world.getBlockLightValue(x,y + 1,z) < 9) return;
        int data=world.getBlockMetadata(x,y,z);
        int ageData=getAgeData(data);
        if(!isGrow(world,x,y,z,random)) return;
        if(ageData < getMaxAge()) {
            world.setBlockMetadataWithNotify(x,y,z,getAge(data,ageData + 1),2);
        } else if(!world.isRemote) {
            world.setBlock(x,y,z, ModBlocks.TOMATO_CROP.getValue(),0,3);
        }
    }

    @Override
    public boolean func_149851_a(World world,int x,int y,int z,boolean isClient) {
        return true;
    }

    @Override
    public void func_149853_b(World world,Random random,int x,int y,int z) {
        int data=getAgeData(world.getBlockMetadata(x,y,z));
        int ageData=1 + random.nextInt(4);
        int age=ageData + data;
        if(age <= getMaxAge()) {
            world.setBlockMetadataWithNotify(x,y,z,age,2);
            return;
        }
        int value=Math.min(3,Math.max(0,age - getMaxAge() - 1));
        world.setBlock(x,y,z,ModBlocks.TOMATO_CROP.getValue(),value,3);
    }

    @Override
    protected Item getSeed() {
        return ModItems.TOMATO_SEEDS.getValue();
    }

    @Override
    protected Item getCrop() {
        return ModItems.TOMATO_SEEDS.getValue();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int data,int fortune) {
        ArrayList<ItemStack> arrayList=new ArrayList<ItemStack>();
        arrayList.add(new ItemStack(ModItems.TOMATO_SEEDS.getValue(),1,0));
        return arrayList;
    }

    @Override
    protected float getStage(int stage) {
        return STAGE[Math.max(0,Math.min(stage,STAGE.length - 1))];
    }
}
