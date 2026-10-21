package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.chaolux.delightlegacy.legacy.common.block.LegacyFarmlandBlock;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class RichSoilFarmlandBlock extends LegacyFarmlandBlock {
    public RichSoilFarmlandBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if(world.isRemote) return;
        int moisture=getMoisture(world.getBlockMetadata(x,y,z));
        if(!isNearWater(world,x,y,z) && !world.canLightningStrikeAt(x,y + 1,z)) {
            if(moisture > 0) {
                world.setBlockMetadataWithNotify(x,y,z,moisture - 1,2);
            } else {
                Block block=world.getBlock(x,y + 1,z);
                boolean isFarmland=block instanceof IPlantable && canSustainPlant(world,x,y,z,ForgeDirection.UP,(IPlantable) block);
                if(!isFarmland) turnToRichSoil(world,x,y,z);
            }
            return;
        }
        if(moisture < 7) {
            world.setBlockMetadataWithNotify(x,y,z,7,2);
            return;
        }
        RichSoilBlock.tryBoostingPlantsAboveAndBelow(world,x,y,z,random);
    }

    private boolean isNearWater(World world,int x,int y,int z) {
        for(int indexX=x - 4;indexX <= x + 4;indexX++) {
            for (int indexZ=z - 4;indexZ <= z + 4;indexZ++) {
                for (int indexY=y;indexY <=y + 1;indexY++) {
                    if(world.getBlock(indexX,indexY,indexZ).getMaterial() == Material.water) return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world,int x,int y,int z,Block block) {
        if(!canSurvive(world,x,y,z)) turnToRichSoil(world,x,y,z);
    }

    @Override
    public void onBlockAdded(World world,int x,int y,int z) {
        super.onBlockAdded(world,x,y,z);
        if(!canSurvive(world,x,y,z)) turnToRichSoil(world,x,y,z);
    }

    private boolean canSurvive(World world,int x,int y,int z) {
        Block block=world.getBlock(x,y + 1,z);
        return !block.getMaterial().isSolid() || block == Blocks.melon_block || block == Blocks.pumpkin;
    }

    public static void turnToRichSoil(World world,int x,int y,int z) {
        world.setBlock(x,y,z, ModBlocks.RICH_SOIL.get(),0,3);
    }

    @Override
    public boolean canSustainPlant(IBlockAccess iBlockAccess, int x, int y, int z, ForgeDirection forgeDirection, IPlantable iPlantable) {
        if(forgeDirection != ForgeDirection.UP) return false;
        EnumPlantType enumPlantType=iPlantable.getPlantType(iBlockAccess,x,y + 1,z);
        return enumPlantType == EnumPlantType.Crop || enumPlantType == EnumPlantType.Plains;
    }

    @Override
    public boolean isFertile(World world,int x,int y,int z) {
        return getMoisture(world.getBlockMetadata(x,y,z)) > 0;
    }

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {

    }

    @Override
    public Item getItemDropped(int value,Random random,int fortune) {
        return Item.getItemFromBlock(ModBlocks.RICH_SOIL.get());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world,int x,int y,int z) {
        return Item.getItemFromBlock(ModBlocks.RICH_SOIL.get());
    }

    public int getMoisture(int value) {
        return Math.max(0,Math.min(value,7));
    }

    @Override
    public int getRenderType() {
        return LegacyModelRenderID.getModelRenderID();
    }
}
