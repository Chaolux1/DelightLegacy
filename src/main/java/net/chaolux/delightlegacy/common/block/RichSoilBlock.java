package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlock;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class RichSoilBlock extends LegacyBlock {
    public RichSoilBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if(world.isRemote) return;
        tryBoostingPlantsAboveAndBelow(world,x,y,z,random);
    }

    public static void tryBoostingPlantsAboveAndBelow(World world,int x,int y,int z,Random random) {
        if(random.nextFloat() > 0.2f) return;
        if(boostPlant(world,x,y + 1,z,random)) return;
        boostPlant(world,x,y - 1,z,random);
    }

    public static boolean boostPlant(World world,int x,int y,int z,Random random) {
        Block block=world.getBlock(x,y,z);
        if(isDirt(block)) return false;
        if(!(block instanceof IGrowable)) return false;
        IGrowable iGrowable=(IGrowable) block;
        if(!iGrowable.func_149851_a(world,x,y,z,false)) return false;
        iGrowable.func_149853_b(world,random,x,y,z);
        for (int index=0;index < 4;index++) {
            world.spawnParticle("happyVillager",x + random.nextFloat(),y + random.nextFloat(),z + random.nextFloat(),0,0,0);
        }
        return true;
    }

    private static boolean isDirt(Block block) {
        return block == Blocks.grass || block == Blocks.tallgrass || block == Blocks.mycelium;
    }

    @Override
    public boolean canSustainPlant(IBlockAccess iBlockAccess, int x, int y, int z, ForgeDirection forgeDirection, IPlantable iPlantable) {
        if(forgeDirection != ForgeDirection.UP) return false;
        EnumPlantType enumPlantType=iPlantable.getPlantType(iBlockAccess,x,y + 1,z);
        return enumPlantType != EnumPlantType.Crop && enumPlantType != EnumPlantType.Nether && enumPlantType != EnumPlantType.Water;
    }

    @Override
    public int getRenderType() {
        return LegacyModelRenderID.getModelRenderID();
    }


}
