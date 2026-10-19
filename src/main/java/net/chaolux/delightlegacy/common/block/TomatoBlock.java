package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.legacy.common.block.LegacyCropBlock;
import net.chaolux.delightlegacy.registry.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class TomatoBlock extends LegacyCropBlock {
    private static final int VINE_AGE=3;
    private static final int ROPELOGGED=4;
    public TomatoBlock() {
        super(3);
    }

    @Override
    public int getAgeData(int data) {
        return data & VINE_AGE;
    }

    @Override
    public int getAge(int data,int age) {
        return (data & ROPELOGGED) | (Math.max(0,Math.min(age,3)) & VINE_AGE);
    }

    @Override
    public boolean canBlockStay(World world,int x,int y,int z) {
        if(world.getBlock(x,y - 1,z) == this) return world.getBlockLightValue(x,y,z) >= 8 || world.canBlockSeeTheSky(x,y,z);
        return super.canBlockStay(world,x,y,z);
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
        if(ageData < getMaxAge() && random.nextInt(6) == 0) world.setBlockMetadataWithNotify(x,y,z,getAge(data,ageData + 1),2);
    }

    @Override
    public void func_149853_b(World world,Random random,int x,int y,int z) {
        int data=world.getBlockMetadata(x,y,z);
        int ageData=getAgeData(data);
        world.setBlockMetadataWithNotify(x,y,z,getAge(data,Math.min(3,ageData + 1 + random.nextInt(2))),2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,int value,float mouseX,float mouseY,float mouseZ) {
        ItemStack itemStack=entityPlayer.getCurrentEquippedItem();
        if(itemStack != null && itemStack.getItem() == Items.dye && itemStack.getItemDamage() == 15) return false;
        int data=world.getBlockMetadata(x,y,z);
        if(!isMaxAge(data)) return false;
        if(!world.isRemote) {
            int age=1 + world.rand.nextInt(2);
            dropBlockAsItem(world,x,y,z,new ItemStack(ModItems.TOMATO.getValue(),age,0));
            world.setBlockMetadataWithNotify(x,y,z,getAge(data,0),3);
        }
        return true;
    }

    @Override
    protected Item getSeed() {
        return ModItems.TOMATO_SEEDS.getValue();
    }

    @Override
    protected Item getCrop() {
        return ModItems.TOMATO.getValue();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int data,int fortune) {
        ArrayList<ItemStack> arrayList=new ArrayList<ItemStack>();
        if(!isRopelogged(data)) arrayList.add(new ItemStack(ModItems.TOMATO_SEEDS.getValue(),1,0));
        if(isMaxAge(data)) {
            int value=1 + world.rand.nextInt(2);
            if(fortune > 0) value += world.rand.nextInt(fortune + 1);
            arrayList.add(new ItemStack(ModItems.TOMATO.getValue(),value,0));
        }
        return arrayList;
    }

    @Override
    protected float getStage(int stage) {
        return 1.0f;
    }

    public boolean isRopelogged(int data) {
        return (data & ROPELOGGED) != 0;
    }

    public int getRopelogged(int data,boolean value) {
        return value ? data | ROPELOGGED : data & ~ROPELOGGED;
    }
}
