package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.common.block.entity.CuttingBoardBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.FeastBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.state.CookingPotSupport;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyBlockStateMapper;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FeastBlock extends BlockContainer {
    public static final int FACING=3;
    public static final String[] FACINGS={"north","east","south","west"};
    public final Supplier<Item> servingItem;
    public final boolean hasLeftovers;
    public final boolean hasServingParticles;
    public static final LegacyBlockStateMapper STATE_MAPPER=new LegacyBlockStateMapper() {
        @Override
        public Map<String, String> getBlockState(IBlockAccess iBlockAccess, int x, int y, int z, Block block, int data) {
            int value=0;
            TileEntity tileEntity=iBlockAccess.getTileEntity(x,y,z);
            if(tileEntity instanceof FeastBlockEntity) value=((FeastBlockEntity)tileEntity).getValue();
            return state(value,Math.min(((FeastBlock)block).getMaxServings(),Math.max(0,data)));
        }

        @Override
        public Map<String, String> getItemState(Block block, int data) {
            return state(0,((FeastBlock)block).getMaxServings());
        }
    };
    public FeastBlock(LegacyBlockProperties legacyBlockProperties,Supplier<Item> servingItem,boolean hasLeftovers,boolean hasServingParticles) {
        super(legacyBlockProperties.getMaterial());
        legacyBlockProperties.from(this);
        this.servingItem=servingItem;
        this.hasLeftovers=hasLeftovers;
        this.hasServingParticles=hasServingParticles;
    }

    public FeastBlock(LegacyBlockProperties legacyBlockProperties,Supplier<Item> servingItem,boolean hasLeftovers) {
        this(legacyBlockProperties,servingItem,hasLeftovers,true);
    }

    @Override
    public TileEntity createNewTileEntity(World world,int value) {
        return new FeastBlockEntity();
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
    public int onBlockPlaced(World world,int x,int y,int z,int value,float mouseX,float mouseY,float mouseZ,int data) {
        return getMaxServings();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        int value=MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0f / 360.0f + 0.5) & FACING;
        if(world.getBlockMetadata(x,y,z) != getMaxServings()) world.setBlockMetadataWithNotify(x,y,z,getMaxServings(),3);
        if(this instanceof GleamingSaladBlock) world.func_147451_t(x,y,z);
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(tileEntity instanceof FeastBlockEntity) ((FeastBlockEntity)tileEntity).setValue(value);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,int value,float mouseX,float mouseY,float mouseZ) {
        int data=getServings(world,x,y,z);
        if(data == 0) {
            if(!world.isRemote) {
                world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"dig.wood",0.8f,0.8f);
                world.func_147480_a(x,y,z,true);
            }
            return true;
        }
        ItemStack itemStack=getServingItem(data);
        if(itemStack == null) return false;
        ItemStack stack=entityPlayer.getCurrentEquippedItem();
        Item item=itemStack.getItem().hasContainerItem() ? itemStack.getItem().getContainerItem() : null;
        if(item != null && (stack == null || stack.getItem() != item)) {
            if(!world.isRemote) entityPlayer.addChatMessage(new ChatComponentTranslation("block.delightlegacy.feast.use_container",new ChatComponentTranslation(item.getUnlocalizedName() + ".name")));
            return true;
        }
        if(world.isRemote && hasServingParticles) {
            int metadata=Block.getIdFromBlock(this);
            for(int index=0;index < 3;index++) {
                world.spawnParticle("blockcrack_" + metadata + "_" + world.getBlockMetadata(x,y,z),x + 0.5 + (world.rand.nextFloat() - 0.5) * 0.2,y + 0.45,z + 0.5 + (world.rand.nextFloat() - 0.5) * 0.2,0,0.02,0);
            }
        }
        if(!world.isRemote) {
            if(item != null && !entityPlayer.capabilities.isCreativeMode) {
                if(--stack.stackSize <= 0) entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem,null);
            }
            if(!entityPlayer.inventory.addItemStackToInventory(itemStack)) entityPlayer.dropPlayerItemWithRandomChoice(itemStack,false);
            int index=data - 1;
            if(index == 0 && !hasLeftovers) {
                world.setBlockToAir(x,y,z);
            } else {
                world.setBlockMetadataWithNotify(x,y,z,index,3);
            }
            if(this instanceof GleamingSaladBlock) world.func_147451_t(x,y,z);
            entityPlayer.inventory.markDirty();
            if(entityPlayer instanceof EntityPlayerMP) ((EntityPlayerMP)entityPlayer).sendContainerToPlayer(entityPlayer.inventoryContainer);
            world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"delightlegacy:block.food.take_portion",1.0f,1.0f);
        }
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World world,int x,int y,int z) {
        return super.canPlaceBlockAt(world,x,y,z) && isSupport(world,x,y,z);
    }

    @Override
    public void onNeighborBlockChange(World world,int x,int y,int z,Block block) {
        if(!world.isRemote && !isSupport(world,x,y,z)) world.func_147480_a(x,y,z,true);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess,int x,int y,int z) {
        int value=getServings(iBlockAccess,x,y,z);
        float data=value == 1 ? 2.0f : (value * 2.0f);
        setBlockBounds(2.0f / 16.0f,0.0f,2.0f / 16.0f,14.0f / 16.0f,data / 16.0f,14.0f / 16.0f);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world,int x,int y,int z,int value) {
        return getServings(world,x,y,z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int value,int fortune) {
        ArrayList<ItemStack> itemStackArrayList=new ArrayList<ItemStack>();
        if(value >= getMaxServings()) {
            Item item=Item.getItemFromBlock(this);
            if(item != null) itemStackArrayList.add(new ItemStack(item));
        } else if(hasLeftovers) {
            itemStackArrayList.add(new ItemStack(Items.bowl));
            if(this == ModBlocks.HONEY_GLAZED_HAM_BLOCK.get()) itemStackArrayList.add(new ItemStack(Items.bone,4));
            if(this == ModBlocks.ROAST_CHICKEN_BLOCK.get()) itemStackArrayList.add(new ItemStack(Items.dye,4,15));
        }
        return itemStackArrayList;
    }

    @Override
    public int damageDropped(int value) {
        return 0;
    }

    protected static Map<String,String> state(int value,int data) {
        Map<String,String> stringStringMap=new LinkedHashMap<String,String>();
        stringStringMap.put("facing",FACINGS[value & FACING]);
        stringStringMap.put("servings",String.valueOf(data));
        return stringStringMap;
    }

    public int getMaxServings() {
        return 4;
    }

    public ItemStack getServingItem(int value) {
        Item item=servingItem.get();
        return item == null ? null : new ItemStack(item);
    }

    public boolean isHasLeftovers() {
        return hasLeftovers;
    }

    protected int getServings(IBlockAccess iBlockAccess,int x,int y,int z) {
        return Math.min(getMaxServings(),Math.max(0,iBlockAccess.getBlockMetadata(x,y,z)));
    }

    protected int getValue(IBlockAccess iBlockAccess,int x,int y,int z) {
        TileEntity tileEntity=iBlockAccess.getTileEntity(x,y,z);
        return tileEntity instanceof FeastBlockEntity ? ((FeastBlockEntity)tileEntity).getValue() : 0;
    }

    protected boolean isSupport(World world,int x,int y,int z) {
        return world.getBlock(x,y - 1,z).isSideSolid(world,x,y - 1,z,ForgeDirection.UP);
    }
}
