package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.common.BlockShapes;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

public class RotatedFeastBlock extends FeastBlock {
    private final double[][][] combinedShapes;
    public RotatedFeastBlock(LegacyBlockProperties legacyBlockProperties, Supplier<Item> servingItem,boolean hasLeftovers,double[][][] combinedShapes) {
        super(legacyBlockProperties,servingItem,hasLeftovers);
        this.combinedShapes=combinedShapes;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess,int x,int y,int z) {
        int value=getServings(iBlockAccess,x,y,z);
        int data=getValue(iBlockAccess,x,y,z);
        double index=2.0;
        if(value > 0) for(double[] metadata : combinedShapes[value - 1]) {
            index=Math.max(index,metadata[4]);
        }
        setBlockBounds(1.0f / 16.0f,0.0f,1.0f / 16.0f,15.0f / 16.0f,(float)index / 16.0f,15.0f / 16.0f);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list, Entity entity) {
        int value=getValue(world,x,y,z);
        for(double[] data : BlockShapes.TRAY_SHAPE) {
            setShapes(x,y,z,value,data,axisAlignedBB,list);
        }
        int metadata=getServings(world,x,y,z);
        if(metadata > 0) for(double[] data : combinedShapes[metadata - 1]) {
            setShapes(x,y,z,value,data,axisAlignedBB,list);
        }
    }

    private static void setShapes(int x,int y,int z,int value,double[] data,AxisAlignedBB axisAlignedBB,List list) {
        double minX=data[0] / 16;
        double maxX=data[3] / 16;
        double minZ=data[2] / 16;
        double maxZ=data[5] / 16;
        for (int index=0;index < (value & FACING);index++) {
            double minY=minX;
            double maxY=maxX;
            minX=1 - maxZ;
            maxX=1 - minZ;
            minZ=minY;
            maxZ=maxY;
        }
        AxisAlignedBB alignedBB=AxisAlignedBB.getBoundingBox(x + minX,y + data[1] / 16,z + minZ,x + maxX,y + data[4] / 16,z + maxZ);
        if(axisAlignedBB.intersectsWith(alignedBB)) list.add(alignedBB);
    }
}
