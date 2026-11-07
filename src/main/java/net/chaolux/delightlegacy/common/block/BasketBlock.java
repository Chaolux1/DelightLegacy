package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.DelightLegacy;
import net.chaolux.delightlegacy.common.block.entity.BasketBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.CookingPotBlockEntity;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyDataStateMapper;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class BasketBlock extends BlockContainer {
    public static final int DOWN=0;
    public static final int UP=1;
    public static final int NORTH=2;
    public static final int SOUTH=3;
    public static final int WEST=4;
    public static final int EAST=5;
    public static final LegacyDataStateMapper STATE_MAPPER=variant();
    public BasketBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties.getMaterial());
        legacyBlockProperties.from(this);
    }

    @Override
    public TileEntity createNewTileEntity(World world,int value) {
        return new BasketBlockEntity();
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
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int data) {
        return LegacyModelManager.getInstance().getParticle(this,data);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        int value;
        if(entityLivingBase.rotationPitch > 45.0f) {
            value=UP;
        } else if(entityLivingBase.rotationPitch < -45.0f) {
            value=DOWN;
        } else {
            int data= MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0f / 360.0f + 0.5f) & 3;
            value=new int[] {NORTH,EAST,SOUTH,WEST}[data];
        }
        world.setBlockMetadataWithNotify(x,y,z,value,3);
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(tileEntity instanceof BasketBlockEntity && itemStack != null && itemStack.hasDisplayName()) ((BasketBlockEntity)tileEntity).setCustomName(itemStack.getDisplayName());
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,int value,float mouseX,float mouseY,float mouseZ) {
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(!(tileEntity instanceof BasketBlockEntity)) return false;
        if(!world.isRemote) entityPlayer.openGui(DelightLegacy.DELIGHT_LEGACY,2,world,x,y,z);
        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if(world.isRemote || !(entity instanceof EntityItem)) return;
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(tileEntity instanceof BasketBlockEntity) ((BasketBlockEntity)tileEntity).onEntityCollision((EntityItem)entity);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block,int value) {
        if(!world.isRemote) {
            TileEntity tileEntity=world.getTileEntity(x,y,z);
            if(tileEntity instanceof BasketBlockEntity) ((BasketBlockEntity)tileEntity).dropItem();
            world.func_147453_f(x,y,z,this);
        }
        super.breakBlock(world,x,y,z,block,value);
    }

    @Override
    public int damageDropped(int value) {
        return 0;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world,int x,int y,int z,int value) {
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        return tileEntity instanceof BasketBlockEntity ? Container.calcRedstoneFromInventory((BasketBlockEntity)tileEntity) : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list,Entity entity) {
        int value=world.getBlockMetadata(x,y,z);
        double data=2.0 / 16.0;
        if(value != WEST) setCollision(x,y,z,0,0,0,data,1,1,axisAlignedBB,list);
        if(value != EAST) setCollision(x,y,z,1 - data,0,0,1,1,1,axisAlignedBB,list);
        if(value != DOWN) setCollision(x,y,z,0,0,0,1,data,1,axisAlignedBB,list);
        if(value != UP) setCollision(x,y,z,0,1 - data,0,1,1,1,axisAlignedBB,list);
        if(value != NORTH) setCollision(x,y,z,0,0,0,1,1,data,axisAlignedBB,list);
        if(value != SOUTH) setCollision(x,y,z,0,0,1 - data,1,1,1,axisAlignedBB,list);
    }

    private static void setCollision(int x,int y,int z,double minX,double minY,double minZ,double maxX,double maxY,double maxZ,AxisAlignedBB axisAlignedBB,List list) {
        AxisAlignedBB alignedBB=AxisAlignedBB.getBoundingBox(x + minX,y + minY,z + minZ,x + maxX,y + maxY,z + maxZ);
        if(alignedBB.intersectsWith(axisAlignedBB))list.add(alignedBB);
    }

    private static LegacyDataStateMapper variant() {
        LegacyDataStateMapper.Builder builder=LegacyDataStateMapper.builder();
        String[] strings={"down","up","north","south","west","east"};
        for (int index=0;index < strings.length;index++) {
            builder.put(index,"facing",strings[index]);
        }
        return builder.build();
    }
}
