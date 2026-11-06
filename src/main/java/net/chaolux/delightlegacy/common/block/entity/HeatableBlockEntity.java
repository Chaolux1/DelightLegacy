package net.chaolux.delightlegacy.common.block.entity;

import net.chaolux.delightlegacy.common.block.AbstractStoveBlock;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.chaolux.delightlegacy.legacy.data.tags.LegacyTagManager;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public interface HeatableBlockEntity {
    default boolean isHeated(World world,int x,int y,int z) {
        if(world == null) return false;
        if(isHeatSource(world,x,y - 1,z)) return true;
        if(!requiresDirectHeat() && isHeatConductor(world,x,y - 1,z)) {
            return isHeatSource(world,x,y - 2,z);
        }
        return false;
    }

    default boolean requiresDirectHeat() {
        return false;
    }

    static boolean isHeatSource(World world,int x,int y,int z) {
        if(world == null || y < 0 || y >= world.getActualHeight()) return false;
        Block block=world.getBlock(x,y,z);
        if(block instanceof AbstractStoveBlock) return AbstractStoveBlock.lit(world.getBlockMetadata(x,y,z));
        if(block == Blocks.fire || block == Blocks.lava || block == Blocks.flowing_lava) return true;
        return LegacyTagManager.getInstance().isBlock(ResourceLocations.resourceLocations("heat_sources"),block,world.getBlockMetadata(x,y,z));
    }

    static boolean isHeatConductor(World world,int x,int y,int z) {
        if(world == null || y < 0 || y >= world.getActualHeight()) return false;
        Block block=world.getBlock(x,y,z);
        if(block == Blocks.hopper) return true;
        return LegacyTagManager.getInstance().isBlock(ResourceLocations.resourceLocations("heat_conductors"),block,world.getBlockMetadata(x,y,z));
    }

    static boolean isTrayHeatSource(World world,int x,int y,int z) {
        if(world == null || y < 0 || y >= world.getActualHeight()) return false;
        Block block=world.getBlock(x,y,z);
        if(block == Blocks.fire || block == Blocks.lava || block == Blocks.flowing_lava) return true;
        return LegacyTagManager.getInstance().isBlock(ResourceLocations.resourceLocations("tray_heat_sources"),block,world.getBlockMetadata(x,y,z));
    }
}
