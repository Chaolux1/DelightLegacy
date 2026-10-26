package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.common.block.entity.CuttingBoardBlockEntity;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CuttingBoardBlock extends BlockContainer {
    public CuttingBoardBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties.getMaterial());
        legacyBlockProperties.from(this);
        setBlockBounds(1.0f / 16.0f,0.0f,1.0f / 16.0f,15.0f / 16.0f,1.0f / 16.0f,15.0f / 16.0f);
    }

    @Override
    public TileEntity createNewTileEntity(World world,int value) {
        return new CuttingBoardBlockEntity();
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
    public IIcon getIcon(int side,int data) {
        return LegacyModelManager.getInstance().getParticle(this,data);
    }

    @Override
    public boolean canPlaceBlockAt(World world,int x,int y,int z) {
        return super.canPlaceBlockAt(world,x,y,z) && world.getBlock(x,y - 1,z).isSideSolid(world,x,y - 1,z, ForgeDirection.UP);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        int value=(MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0f / 360.0f + 0.5) + 2) & 3;
        world.setBlockMetadataWithNotify(x,y,z,value,2);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if(!world.isRemote && !canPlaceBlockAt(world,x,y,z)) world.func_147480_a(x,y,z,true);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,int value,float mouseX,float mouseY,float mouseZ) {
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(!(tileEntity instanceof CuttingBoardBlockEntity)) return false;
        CuttingBoardBlockEntity cuttingBoardBlockEntity=(CuttingBoardBlockEntity)tileEntity;
        ItemStack itemStack=entityPlayer.getCurrentEquippedItem();
        if(itemStack == null) {
            if(!world.isRemote && !cuttingBoardBlockEntity.isEmpty()) {
                ItemStack stack=cuttingBoardBlockEntity.removeItem();
                if(stack != null) {
                    int data=entityPlayer.inventory.currentItem;
                    if(entityPlayer.inventory.getStackInSlot(data) == null) {
                        entityPlayer.inventory.setInventorySlotContents(data,stack);
                    } else {
                        entityPlayer.inventory.addItemStackToInventory(stack);
                        if(stack.stackSize > 0) entityPlayer.dropPlayerItemWithRandomChoice(stack,false);
                    }
                    entityPlayer.inventory.markDirty();
                }
                world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"random.pop",0.25f,0.5f);
            }
            return true;
        }
        if(entityPlayer.isSneaking() && cuttingBoardBlockEntity.isCarveTool(itemStack)) {
            if(!world.isRemote && cuttingBoardBlockEntity.carveToolOnBoard(itemStack)) {
                if(!entityPlayer.capabilities.isCreativeMode) {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem,null);
                    entityPlayer.inventory.markDirty();
                }
                world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"dig.wood",0.8f,0.8f);
            }
            return true;
        }
        if(cuttingBoardBlockEntity.canAddItem(itemStack)) {
            if(!world.isRemote) {
                ItemStack stack=cuttingBoardBlockEntity.addItem(itemStack);
                if(!entityPlayer.capabilities.isCreativeMode) {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem,stack);
                    entityPlayer.inventory.markDirty();
                }
                world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"dig.wood",0.8f,0.8f);
            }
            return true;
        }
        if(!world.isRemote) cuttingBoardBlockEntity.processStoredItemUsingTool(itemStack,entityPlayer);
        return true;
    }

    @Override
    public void breakBlock(World world,int x,int y,int z,Block block,int value) {
        if(!world.isRemote) {
            TileEntity tileEntity=world.getTileEntity(x,y,z);
            if(tileEntity instanceof CuttingBoardBlockEntity) ((CuttingBoardBlockEntity)tileEntity).dropItemStack();
            world.func_147453_f(x,y,z,this);
        }
        super.breakBlock(world,x,y,z,block,value);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world,int x,int y,int z,int value) {
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(!(tileEntity instanceof CuttingBoardBlockEntity)) return 0;
        ItemStack itemStack=((CuttingBoardBlockEntity)tileEntity).getStoredItem();
        if(itemStack == null) return 0;
        int data=Math.min(64,itemStack.getMaxStackSize());
        return MathHelper.floor_double((float)itemStack.stackSize / Math.max(1,data) * 14.0f) + 1;
    }
}
