/*
Copyright (c) Forge Development LLC and contributors
SPDX-License-Identifier: LGPL-2.1-only
Modified by Chaolux, 2026
 */
package net.chaolux.delightlegacy.legacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Random;

public abstract class LegacyCropBlock extends BlockCrops {
    private final int maxAge;
    protected LegacyCropBlock(int maxAge) {
        if(maxAge < 1 || maxAge > 7) throw new IllegalArgumentException("Crop max must be between 1 and 7");
        this.maxAge=maxAge;
        setTickRandomly(true);
        setHardness(0.0f);
        setStepSound(soundTypeGrass);
        disableStats();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iIconRegister) {
        blockIcon=iIconRegister.registerIcon("minecraft:wheat_stage_0");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side,int data) {
        return blockIcon;
    }

    @Override
    public void updateTick(World world,int x,int y,int z,Random random) {
        int data=world.getBlockMetadata(x,y,z);
        if(!canBlockStay(world,x,y,z)) {
            if(!world.isRemote) {
                dropBlockAsItem(world,x,y,z,data,0);
                world.setBlockToAir(x,y,z);
            }
            return;
        }
        if(world.getBlockLightValue(x,y + 1,z) < 9) return;
        int ageData=getAgeData(data);
        if(ageData >= maxAge) return;
        if(!isGrow(world,x,y,z,random)) return;
        world.setBlockMetadataWithNotify(x,y,z,getAge(data,ageData + 1),2);
    }

    @Override
    public boolean func_149851_a(World world,int x,int y,int z,boolean isClient) {
        return !isMaxAge(world.getBlockMetadata(x,y,z));
    }

    @Override
    public boolean func_149852_a(World world,Random random,int x,int y,int z) {
        return true;
    }

    @Override
    public void func_149853_b(World world,Random random,int x,int y,int z) {
        int data=world.getBlockMetadata(x,y,z);
        int ageData=getAgeData(data);
        int age=ageData + getBonemealAgeIncrease(random);
        if(age > maxAge) age=maxAge;
        world.setBlockMetadataWithNotify(x,y,z,getAge(data,age),2);
    }

    @Override
    protected Item func_149866_i() {
        return getSeed();
    }

    @Override
    protected Item func_149865_P() {
        return getCrop();
    }

    @Override
    public Item getItem(World world,int x,int y,int z) {
        return getSeed();
    }

    @Override
    public Item getItemDropped(int data,Random random,int fortune) {
        return getSeed();
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public void dropBlockAsItemWithChance(World world,int x,int y,int z,int data,float chance,int fortune) {
        if(world.isRemote) return;
        ArrayList<ItemStack> itemStacks=getDrops(world,x,y,z,data,fortune);
        for(ItemStack itemStack : itemStacks) {
            if(itemStack == null || itemStack.stackSize <= 0) continue;
            if(world.rand.nextFloat() > chance) continue;
            dropBlockAsItem(world,x,y,z,itemStack);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int data,int fortune) {
        ArrayList<ItemStack> itemStacks=new ArrayList<ItemStack>();
        Item seed=getSeed();
        Item crop=getCrop();
        int ageData=getAgeData(data);
        if(ageData >= maxAge) {
            if(crop != null) itemStacks.add(new ItemStack(crop,1,0));
            if(seed != null) {
                for (int index=0;index < 3 + Math.max(0,fortune);index++) {
                    if(world.rand.nextInt(15) <= ageData) itemStacks.add(new ItemStack(seed,1,0));
                }
            }
        } else if(seed != null) {
            itemStacks.add(new ItemStack(seed,1,0));
        }
        return itemStacks;
    }

    @Override
    public int getRenderType() {
        return LegacyModelRenderID.getModelRenderID();
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess iBlockAccess,int x,int y,int z) {
        return EnumPlantType.Crop;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess,int x,int y,int z) {
        int data=iBlockAccess.getBlockMetadata(x,y,z);
        int ageData=getAgeData(data);
        float stage=getStage(ageData);
        setBlockBounds(0.0f,0.0f,0.0f,1.0f,stage,1.0f);
    }

    public final int getMaxAge() {
        return maxAge;
    }

    public int getAgeData(int data) {
        return MathHelper.clamp_int(data,0,maxAge);
    }

    public int getAge(int data,int age) {
        return MathHelper.clamp_int(age,0,maxAge);
    }

    public boolean isMaxAge(int data) {
        return getAgeData(data) >= maxAge;
    }

    protected int getBonemealAgeIncrease(Random random) {
        return MathHelper.getRandomIntegerInRange(random,2,5);
    }

    protected float getGrowthSpeed(World world,int x,int y,int z) {
        float speed=1.0f;
        for(int indexX = -1;indexX <= 1;indexX++) {
            for(int indexZ = -1; indexZ <= 1;indexZ++) {
                float growth=0.0f;
                int offsetX=x + indexX;
                int offsetY=y - 1;
                int offsetZ=z + indexZ;
                Block block=world.getBlock(offsetX,offsetY,offsetZ);
                if(block != null && block.canSustainPlant(world,offsetX,offsetY,offsetZ, ForgeDirection.UP,this)) {
                    growth=1.0f;
                    if(block.isFertile(world,offsetX,offsetY,offsetZ)) growth=3.0f;
                }
                if(indexX != 0 || indexZ != 0) growth /= 4.0f;
                speed += growth;
            }
        }
        boolean worldX=world.getBlock(x - 1,y,z) == this || world.getBlock(x + 1,y,z) == this;
        boolean worldZ=world.getBlock(x,y,z - 1) == this || world.getBlock(x,y,z + 1) == this;
        if(worldX && worldZ) {
            speed /= 2.0f;
        } else {
            boolean worldBlock=world.getBlock(x - 1,y,z - 1) == this || world.getBlock(x + 1,y,z - 1) == this || world.getBlock(x + 1,y,z + 1) == this || world.getBlock(x - 1,y,z + 1) == this;
            if(worldBlock) speed /= 2.0f;
        }
        return speed;
    }

    protected boolean isGrow(World world,int x,int y,int z,Random random) {
        float speed=getGrowthSpeed(world,x,y,z);
        int value=(int) (25.0f / speed) + 1;
        return random.nextInt(Math.max(1,value)) == 0;
    }

    protected float getStage(int stage) {
        return Math.min(1.0f,(2.0f + stage * 2.0f) / 16.0f);
    }

    protected abstract Item getSeed();
    protected abstract Item getCrop();

}
