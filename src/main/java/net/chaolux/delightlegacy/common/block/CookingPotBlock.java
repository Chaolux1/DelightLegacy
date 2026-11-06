package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.DelightLegacy;
import net.chaolux.delightlegacy.common.block.entity.CookingPotBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.HeatableBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.state.CookingPotSupport;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyDataStateMapper;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class CookingPotBlock extends BlockContainer {
    public static final int FACING=3;
    public static final LegacyDataStateMapper STATE_MAPPER=variant();
    public CookingPotBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties.getMaterial());
        legacyBlockProperties.from(this);
        setBlockBounds(2.0f / 16.0f,0,2.0f / 16.0f,14.0f / 16.0f,10.0f / 16.0f,14.0f / 16.0f);
    }

    @Override
    public TileEntity createNewTileEntity(World world,int value) {
        return new CookingPotBlockEntity();
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
    public int getRenderType() {
        return LegacyModelRenderID.getModelRenderID();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int data) {
        return LegacyModelManager.getInstance().getParticle(this,data);
    }

    @Override
    public int onBlockPlaced(World world,int x,int y,int z,int value,float mouseX,float mouseY,float mouseZ,int data) {
        CookingPotSupport cookingPotSupport=value == 0 ? CookingPotSupport.HANDLE : getTrayState(world,x,y,z);
        return cookingPotSupport.getValue();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        int value=(MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0f / 360.0f + 0.5) + 2) & FACING;
        CookingPotSupport cookingPotSupport=getCookingPotSupport(world.getBlockMetadata(x,y,z));
        world.setBlockMetadataWithNotify(x,y,z,value | cookingPotSupport.getValue(),3);
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(tileEntity instanceof CookingPotBlockEntity && itemStack != null) {
            CookingPotBlockEntity cookingPotBlockEntity=(CookingPotBlockEntity)tileEntity;
            if(itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("BlockEntityTag",10))cookingPotBlockEntity.readMeal(itemStack.getTagCompound().getCompoundTag("BlockEntityTag"));
            if(itemStack.hasDisplayName())cookingPotBlockEntity.setCustomName(itemStack.getDisplayName());
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if(world.isRemote) return;
        int value=world.getBlockMetadata(x,y,z);
        if(getCookingPotSupport(value) == CookingPotSupport.HANDLE) return;
        CookingPotSupport cookingPotSupport=getTrayState(world,x,y,z);
        if(getCookingPotSupport(value) != cookingPotSupport) world.setBlockMetadataWithNotify(x,y,z,(value & FACING) | cookingPotSupport.getValue(),3);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,int value,float mouseX,float mouseY,float mouseZ) {
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(!(tileEntity instanceof CookingPotBlockEntity)) return false;
        if(entityPlayer.isSneaking() && entityPlayer.getCurrentEquippedItem() == null) {
            if(!world.isRemote) {
                int data=world.getBlockMetadata(x,y,z);
                CookingPotSupport cookingPotSupport=getCookingPotSupport(data) == CookingPotSupport.HANDLE ? getTrayState(world,x,y,z) : CookingPotSupport.HANDLE;
                world.setBlockMetadataWithNotify(x,y,z,(data & FACING) | cookingPotSupport.getValue(),3);
                world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"random.click",0.6f,1.0f);
            }
            return true;
        }
        if(!world.isRemote) {
            CookingPotBlockEntity cookingPotBlockEntity=(CookingPotBlockEntity)tileEntity;
            ItemStack itemStack=cookingPotBlockEntity.useHeldItemOnMeal(entityPlayer.getCurrentEquippedItem(),entityPlayer.capabilities.isCreativeMode);
            if(itemStack != null) {
                ItemStack stack=entityPlayer.getCurrentEquippedItem();
                if(stack != null && stack.stackSize <= 0) entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem,null);
                if(!entityPlayer.inventory.addItemStackToInventory(itemStack)) entityPlayer.dropPlayerItemWithRandomChoice(itemStack,false);
                entityPlayer.inventory.markDirty();
                world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"random.pop",0.7f,1.1f);
            } else {
                entityPlayer.openGui(DelightLegacy.DELIGHT_LEGACY,0,world,x,y,z);
            }
        }
        return true;
    }

    @Override
    public void onBlockHarvested(World world,int x,int y,int z,int value,EntityPlayer entityPlayer) {
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(tileEntity instanceof CookingPotBlockEntity) ((CookingPotBlockEntity)tileEntity).setCreative(entityPlayer.capabilities.isCreativeMode);
        super.onBlockHarvested(world,x,y,z,value,entityPlayer);
    }

    @Override
    public void breakBlock(World world,int x,int y,int z,Block block,int value) {
        if(!world.isRemote) {
            TileEntity tileEntity=world.getTileEntity(x,y,z);
            if(tileEntity instanceof CookingPotBlockEntity) {
                CookingPotBlockEntity cookingPotBlockEntity=(CookingPotBlockEntity)tileEntity;
                if(!cookingPotBlockEntity.isCreative()) {
                    ItemStack itemStack=new ItemStack(this);
                    if(cookingPotBlockEntity.hasCustomInventoryName()) itemStack.setStackDisplayName(cookingPotBlockEntity.getInventoryName());
                    NBTTagCompound nbtTagCompound=cookingPotBlockEntity.writeMeal(new NBTTagCompound());
                    if(!nbtTagCompound.hasNoTags()) {
                        NBTTagCompound tagCompound=new NBTTagCompound();
                        tagCompound.setTag("BlockEntityTag",nbtTagCompound);
                        itemStack.setTagCompound(tagCompound);
                    }
                    dropItem(world,x,y,z,itemStack);
                    cookingPotBlockEntity.dropItem();
                }
                world.func_147453_f(x,y,z,this);
            }
        }
        super.breakBlock(world,x,y,z,block,value);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world,int x,int y,int z,int value) {
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        return tileEntity instanceof CookingPotBlockEntity ? ((CookingPotBlockEntity)tileEntity).getComparatorLevel() : 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world,int x,int y,int z,Random random) {
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(!(tileEntity instanceof CookingPotBlockEntity) || !((CookingPotBlockEntity)tileEntity).isHeated()) return;
        if(random.nextInt(5) == 0) world.spawnParticle("splash",x + 0.25 + random.nextDouble() * 0.5,y + 0.58,z + 0.25 + random.nextDouble() * 0.5,0,0.01,0);
        if(random.nextInt(20) == 0) world.spawnParticle("smoke",x + 0.3 + random.nextDouble() * 0.4,y + 0.7,z + 0.3 + random.nextDouble() * 0.4,0,0.02,0);
        if(random.nextInt(10) == 0) {
            boolean value=((CookingPotBlockEntity)tileEntity).getStackInSlot(CookingPotBlockEntity.MEAL_DISPLAY_SLOT) != null;
            world.playSound(x + 0.5,y + 0.5,z + 0.5,"delightlegacy:block.cooking_pot." + (value ? "boil_soup" : "boil"),0.5f,0.9f + random.nextFloat() * 0.2f,false);
        }
    }

    public static CookingPotSupport getCookingPotSupport(int value) {
        return CookingPotSupport.from(value);
    }

    public static CookingPotSupport getTrayState(World world,int x,int y,int z) {
        return HeatableBlockEntity.isTrayHeatSource(world,x,y - 1,z) ? CookingPotSupport.TRAY : CookingPotSupport.NONE;
    }

    private static void dropItem(World world,int x,int y,int z,ItemStack itemStack) {
        if(itemStack == null || itemStack.stackSize <= 0) return;
        EntityItem entityItem=new EntityItem(world,x + 0.5,y + 0.5,z + 0.5,itemStack);
        entityItem.delayBeforeCanPickup=10;
        world.spawnEntityInWorld(entityItem);
    }

    private static LegacyDataStateMapper variant() {
        LegacyDataStateMapper.Builder builder=LegacyDataStateMapper.builder();
        String[] strings={"north","east","south","west"};
        String[] support={"none","tray","handle"};
        for (int index=0;index < 12;index++) {
            builder.put(index,"facing",strings[index & FACING]);
            builder.put(index,"support",support[index >> 2]);
        }
        return builder.build();
    }
}
