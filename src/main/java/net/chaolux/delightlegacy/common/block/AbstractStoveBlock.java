package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.common.block.entity.AbstractStoveBlockEntity;
import net.chaolux.delightlegacy.common.utility.ItemUtils;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public abstract class AbstractStoveBlock extends BlockContainer {
    public static final int LIT=4;
    public static final int FACING=3;
    protected AbstractStoveBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties.getMaterial());
        legacyBlockProperties.from(this);
    }

    @Override
    public int getRenderType() {
        return LegacyModelRenderID.getModelRenderID();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int data) {
        return LegacyModelManager.getInstance().getParticle(this,data);
    }

    @Override
    public int getLightValue(IBlockAccess iBlockAccess,int x,int y,int z) {
        return lit(iBlockAccess.getBlockMetadata(x,y,z)) ? 13 : 0;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        int value=(MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0f / 360.0f + 0.5) + 2) & FACING;
        world.setBlockMetadataWithNotify(x,y,z,value | LIT,3);
        world.func_147451_t(x,y,z);
    }

    @Override
    public int damageDropped(int value) {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,int value,float mouseX,float mouseY,float mouseZ) {
        ItemStack itemStack=entityPlayer.getCurrentEquippedItem();
        boolean data=lit(world.getBlockMetadata(x,y,z));
        if(itemStack != null && data && (ItemUtils.isToolAction(itemStack,"shovel_dig") || itemStack.getItem() == Items.water_bucket)) {
            if(!world.isRemote) {
                setLit(world,x,y,z,false);
                world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"random.fizz",0.6f,1.0f);
                if(!entityPlayer.capabilities.isCreativeMode) {
                    if(itemStack.getItem() == Items.water_bucket) {
                        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem,new ItemStack(Items.bucket));
                    } else {
                        itemStack.damageItem(1,entityPlayer);
                    }
                    entityPlayer.inventory.markDirty();
                }
            }
            return true;
        }
        if(itemStack != null && !data && (itemStack.getItem() == Items.flint_and_steel || itemStack.getItem() == Items.fire_charge)) {
            if(!world.isRemote) {
                setLit(world,x,y,z,true);
                world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"fire.ignite",0.9f,1.0f);
                if(!entityPlayer.capabilities.isCreativeMode) {
                    if(itemStack.getItem() == Items.flint_and_steel) {
                        itemStack.damageItem(1,entityPlayer);
                    } else if(--itemStack.stackSize <= 0) {
                        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem,null);
                    }
                    entityPlayer.inventory.markDirty();
                }
            }
            return true;
        }
        if(itemStack == null || isStoveTopCovered(world,x,y,z)) return false;
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(!(tileEntity instanceof AbstractStoveBlockEntity)) return false;
        AbstractStoveBlockEntity abstractStoveBlockEntity=(AbstractStoveBlockEntity)tileEntity;
        if(!abstractStoveBlockEntity.getItem(itemStack)) return false;
        if(!world.isRemote && abstractStoveBlockEntity.placeFood(itemStack,entityPlayer.capabilities.isCreativeMode)) {
            if(itemStack.stackSize <= 0) entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem,null);
            entityPlayer.inventory.markDirty();
            world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"random.pop",0.4f,1.1f);
        }
        return true;
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        if(!world.isRemote && lit(world.getBlockMetadata(x,y,z)) && entity instanceof EntityLivingBase && !entity.isSneaking()) {
            AxisAlignedBB axisAlignedBB=AxisAlignedBB.getBoundingBox(x + 3.0 / 16.0,y + 1,z + 3.0 / 16.0,x + 13.0 / 16.0,y + 1.0625,z + 13.0 / 16.0);
            if(entity.boundingBox.intersectsWith(axisAlignedBB)) entity.attackEntityFrom(DamageSource.inFire,1.0f);
        }
        super.onEntityWalking(world,x,y,z,entity);
    }

    @Override
    public void breakBlock(World world,int x,int y,int z,Block block,int value) {
        if(!world.isRemote) {
            TileEntity tileEntity=world.getTileEntity(x,y,z);
            if(tileEntity instanceof AbstractStoveBlockEntity) ((AbstractStoveBlockEntity)tileEntity).drop();
        }
        super.breakBlock(world,x,y,z,block,value);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if(!lit(world.getBlockMetadata(x,y,z))) return;
        int value=getFacing(world.getBlockMetadata(x,y,z));
        double data=random.nextDouble() * 0.6 - 0.3;
        double mouseX=x + 0.5 + (value == 1 ? 0.52 : value == 3 ? -0.52 : data);
        double mouseY=y + random.nextDouble() * 0.375;
        double mouseZ=z + 0.5 + (value == 2 ? 0.52 : value == 0 ? -0.52 : data);
        world.spawnParticle("smoke",mouseX,mouseY,mouseZ,0,0,0);
        world.spawnParticle("flame",mouseX,mouseY,mouseZ,0,0,0);
        if(random.nextInt(10) == 0) world.playSound(mouseX,mouseY,mouseZ,"fire.fire",0.7f,1.0f,false);

    }

    public static boolean lit(int value) {
        return (value & LIT) != 0;
    }

    public static int getFacing(int value) {
        return value & FACING;
    }

    protected void setLit(World world,int x,int y,int z,boolean data) {
        int value=world.getBlockMetadata(x,y,z);
        world.setBlockMetadataWithNotify(x,y,z,(value & FACING) | (data ? LIT : 0),FACING);
        world.func_147451_t(x,y,z);
    }

    public static boolean isStoveTopCovered(World world,int x,int y,int z) {
        Block block=world.getBlock(x,y + 1,z);
        if(block.isAir(world,x,y + 1,z)) return false;
        AxisAlignedBB axisAlignedBB=block.getCollisionBoundingBoxFromPool(world,x,y + 1,z);
        if(axisAlignedBB == null) return false;
        AxisAlignedBB alignedBB=AxisAlignedBB.getBoundingBox(x + 3.0 / 16.0,y + 1,z + 3.0 / 16.0,x + 13.0 / 16.0,y + 1.0625,z + 13.0 / 16.0);
        return alignedBB.intersectsWith(axisAlignedBB);
    }
}
