package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.common.block.entity.CuttingBoardBlockEntity;
import net.chaolux.delightlegacy.common.utility.ItemUtils;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyDataStateMapper;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.chaolux.delightlegacy.legacy.common.item.LegacyItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PieBlock extends Block {
    public static final int FACING=3;
    public static final LegacyDataStateMapper STATE_MAPPER=variant();
    public final Supplier<Item> pieSlice;
    public PieBlock(LegacyBlockProperties legacyBlockProperties,Supplier<Item> pieSlice) {
        super(legacyBlockProperties.getMaterial());
        legacyBlockProperties.from(this);
        this.pieSlice=pieSlice;
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase,ItemStack itemStack) {
        int value=(MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0f / 360.0f + 0.5)) & FACING;
        world.setBlockMetadataWithNotify(x,y,z,value,3);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,int value,float mouseX,float mouseY,float mouseZ) {
        ItemStack itemStack=entityPlayer.getCurrentEquippedItem();
        if(ItemUtils.isKnife(itemStack)) return cutSlice(world,x,y,z,entityPlayer,itemStack);
        return consumeBite(world,x,y,z,entityPlayer);
    }

    @Override
    public boolean canPlaceBlockAt(World world,int x,int y,int z) {
        return super.canPlaceBlockAt(world,x,y,z) && isSupport(world,x,y,z);
    }

    @Override
    public void onNeighborBlockChange(World world,int x,int y,int z,Block block) {
        if(!world.isRemote && !isSupport(world,x,y,z)) world.func_147480_a(x,y,z,true);
    }

    private boolean isSupport(World world,int x,int y,int z) {
        return world.getBlock(x,y - 1,z).isSideSolid(world,x,y - 1,z, ForgeDirection.UP);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess,int x,int y,int z) {
        int value=iBlockAccess.getBlockMetadata(x,y,z);
        int data=(value >> 2) & 3;
        int metadata=value & FACING;
        float minX=data == 3 ? 8.0f : 2.0f;
        float maxX=14.0f;
        float minZ=2.0f;
        float maxZ=data >= 2 ? 8.0f : 14.0f;
        for(int index=0;index < metadata;index++) {
            float minY=minX;
            float maxY=maxX;
            minX=16.0f - maxZ;
            maxX=16.0f - minZ;
            minZ=minY;
            maxZ=maxY;
        }
        setBlockBounds(minX / 16.0f,0.0f,minZ / 16.0f,maxX / 16.0f,4.0f / 16.0f,maxZ / 16.0f);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list, Entity entity) {
        int value=world.getBlockMetadata(x,y,z);
        int data=(value >> 2) & 3;
        int metadata=value & FACING;
        if(data == 0) {
            setShapes(x,y,z,metadata,new double[]{2,0,2,14,4,14},axisAlignedBB,list);
        } else if(data == 1) {
            setShapes(x,y,z,metadata,new double[]{2,0,8,8,4,14},axisAlignedBB,list);
            setShapes(x,y,z,metadata,new double[]{2,0,2,14,4,8},axisAlignedBB,list);
        } else if(data == 2) {
            setShapes(x,y,z,metadata,new double[]{2,0,2,14,4,8},axisAlignedBB,list);
        } else {
            setShapes(x,y,z,metadata,new double[]{8,0,2,14,4,8},axisAlignedBB,list);
        }
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world,int x,int y,int z,int value) {
        return getMaxBites() - ((world.getBlockMetadata(x,y,z) >> 2) & 3);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int value,int fortune) {
        ArrayList<ItemStack> itemStackArrayList=new ArrayList<ItemStack>();
        if((value >> 2) != 0) return itemStackArrayList;
        Item item=getPie();
        if(item != null) itemStackArrayList.add(new ItemStack(item));
        return itemStackArrayList;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition movingObjectPosition,World world,int x,int y,int z,EntityPlayer entityPlayer) {
        Item item=getPie();
        return item == null ? null : new ItemStack(item);
    }

    @Override
    public int damageDropped(int value) {
        return 0;
    }

    public ItemStack getPieSliceItem() {
        Item item=pieSlice.get();
        return item == null ? null : new ItemStack(item);
    }

    public int getMaxBites() {
        return 4;
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

    protected Item getPie() {
        return Item.getItemFromBlock(this);
    }

    protected boolean consumeBite(World world,int x,int y,int z,EntityPlayer entityPlayer) {
        if(!entityPlayer.canEat(false)) return false;
        ItemStack itemStack=getPieSliceItem();
        if(itemStack == null || !(itemStack.getItem() instanceof LegacyItem) || !((LegacyItem)itemStack.getItem()).isFood()) return false;
        if(!world.isRemote) {
            itemStack.getItem().onEaten(itemStack,world,entityPlayer);
            setSlice(world,x,y,z);
            world.playSoundEffect(x + 0.5,y + 0.3,z + 0.5,"random.eat",0.8f,0.8f);
        }
        return true;
    }

    protected boolean cutSlice(World world,int x,int y,int z,EntityPlayer entityPlayer,ItemStack itemStack) {
        ItemStack stack=getPieSliceItem();
        if(stack == null) return false;
        if(!world.isRemote) {
            int value=MathHelper.floor_double(entityPlayer.rotationYaw * 4.0f / 360.0f + 0.5) & FACING;
            int[] mouseX={0,1,0,-1};
            int[] mouseZ={-1,0,1,0};
            EntityItem entityItem=new EntityItem(world,x + 0.5,y + 0.3,z + 0.5,stack);
            entityItem.motionX=mouseX[value] * 0.15;
            entityItem.motionY=0.05;
            entityItem.motionZ=mouseZ[value] * 0.15;
            entityItem.delayBeforeCanPickup=10;
            world.spawnEntityInWorld(entityItem);
            setSlice(world,x,y,z);
            world.playSoundEffect(x + 0.5,y + 0.3,z + 0.5,"delightlegacy:block.food.slice",0.8f,0.8f);
        }
        return true;
    }

    protected void setSlice(World world,int x,int y,int z) {
        int value=world.getBlockMetadata(x,y,z);
        int data=(value >> 2) & 3;
        if(data < getMaxBites() - 1) {
            world.setBlockMetadataWithNotify(x,y,z,(value & FACING) | ((data + 1) << 2),3);
        } else {
            world.setBlockToAir(x,y,z);
        }
    }

    private static LegacyDataStateMapper variant() {
        LegacyDataStateMapper.Builder builder=LegacyDataStateMapper.builder();
        String[] strings={"north","east","south","west"};
        for (int index=0;index < 16;index++) {
            builder.put(index,"bites",String.valueOf((index >> 2) & 3));
            builder.put(index,"facing",strings[index & FACING]);
        }
        return builder.build();
    }
}
