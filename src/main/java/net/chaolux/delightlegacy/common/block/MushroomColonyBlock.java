package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.common.item.MushroomColonyItem;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderer;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBushBlock;
import net.chaolux.delightlegacy.legacy.registry.LegacyItemBlock;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class MushroomColonyBlock extends LegacyBushBlock implements IGrowable, LegacyItemBlock {
    public static final int PLACING_LIGHT_LEVEL = 13;
    public static final int COLONY_AGE=3;
    private static final float[] MIN={4.0f / 16.0f,3.0f / 16.0f,2.0f / 16.0f,1.0f / 16.0f};
    private static final float[] MAX={12.0f / 16.0f,13.0f / 16.0f,14.0f / 16.0f,15.0f / 16.0f};
    private static final float[] STAGE={8.0f / 16.0f,10.0f / 16.0f,12.0f / 16.0f,14.0f / 16.0f};
    private final Block mushroomType;
    public MushroomColonyBlock(LegacyBlockProperties legacyBlockProperties, Block mushroomType) {
        super(legacyBlockProperties);
        this.mushroomType=mushroomType;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess,int x,int y,int z) {
        int value=getColonyAge(iBlockAccess.getBlockMetadata(x,y,z));
        setBlockBounds(MIN[value],0.0f,MIN[value],MAX[value],STAGE[value],MAX[value]);
    }

    @Override
    public boolean canBlockStay(World world,int x,int y,int z) {
        Block block=world.getBlock(x,y - 1,z);
        int value=world.getBlockMetadata(x,y - 1,z);
        if(isGrow(block,value)) return true;
        return world.getBlockLightValue(x,y,z) < PLACING_LIGHT_LEVEL && block.canSustainPlant(world,x,y - 1,z, ForgeDirection.UP,this);
    }

    @Override
    public boolean canPlaceBlockAt(World world,int x,int y,int z) {
        return world.isAirBlock(x,y,z) && canBlockStay(world,x,y,z);
    }

    @Override
    public void onNeighborBlockChange(World world,int x,int y,int z,Block block) {
        if(canBlockStay(world,x,y,z)) return;
        if(!world.isRemote) {
            int value=world.getBlockMetadata(x,y,z);
            dropBlockAsItem(world,x,y,z,value,0);
            world.setBlockToAir(x,y,z);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if(!canBlockStay(world,x,y,z)) {
            if(!world.isRemote) {
                int value=world.getBlockMetadata(x,y,z);
                dropBlockAsItem(world,x,y,z,value,0);
                world.setBlockToAir(x,y,z);
            }
            return;
        }
        if(world.isRemote) return;
        int value=getColonyAge(world.getBlockMetadata(x,y,z));
        if(value >= COLONY_AGE) return;
        if(world.getBlock(x,y - 1,z) != ModBlocks.RICH_SOIL.get()) return;
        if(random.nextInt(4) != 0) return;
        world.setBlockMetadataWithNotify(x,y,z,value + 1,2);
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess iBlockAccess,int x,int y,int z) {
        return EnumPlantType.Cave;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,int value,float mouseX,float mouseY,float mouseZ) {
        ItemStack itemStack=entityPlayer.getCurrentEquippedItem();
        int stage=getColonyAge(world.getBlockMetadata(x,y,z));
        if(stage <= 0 || itemStack == null || itemStack.getItem() != Items.shears) return false;
        if(!world.isRemote) {
            world.setBlockMetadataWithNotify(x,y,z,stage - 1,2);
            dropBlockAsItem(world,x,y,z,new ItemStack(getMushroom(),1,0));
            if(!entityPlayer.capabilities.isCreativeMode) itemStack.damageItem(1,entityPlayer);
            world.playSoundEffect(x + 0.5,y + 0.5,z + 0.5,"mob.sheep.shear",1.0f,1.0f);
        }
        return true;
    }

    @Override
    public void harvestBlock(World world,EntityPlayer entityPlayer,int x,int y,int z,int value) {
        ItemStack itemStack=entityPlayer.getCurrentEquippedItem();
        if(getColonyAge(value) == COLONY_AGE && itemStack != null && itemStack.getItem() == Items.shears) {
            if(!world.isRemote) {
                Item item=Item.getItemFromBlock(this);
                if(item != null) dropBlockAsItem(world,x,y,z,new ItemStack(item,1,0));
            }
            return;
        }
        super.harvestBlock(world,entityPlayer,x,y,z,value);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int value,int fortune) {
        ArrayList<ItemStack> arrayList=new ArrayList<ItemStack>();
        arrayList.add(new ItemStack(getMushroom(),2 + getColonyAge(value),0));
        return arrayList;
    }

    @Override
    public Item getItem(World world,int x,int y,int z) {
        return getMushroom();
    }

    @Override
    public boolean func_149851_a(World world,int x,int y,int z,boolean client) {
        return getColonyAge(world.getBlockMetadata(x,y,z)) < COLONY_AGE;
    }

    @Override
    public boolean func_149852_a(World world,Random random,int x,int y,int z) {
        return true;
    }

    @Override
    public void func_149853_b(World world,Random random,int x,int y,int z) {
        int value=getColonyAge(world.getBlockMetadata(x,y,z));
        if(value >= COLONY_AGE) return;
        int data=1 + random.nextInt(2);
        world.setBlockMetadataWithNotify(x,y,z,Math.min(COLONY_AGE,value + data),2);
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
    public Class<? extends ItemBlock> getItemBlock() {
        return MushroomColonyItem.class;
    }

    public int getColonyAge(int value) {
        return Math.max(0,Math.min(value,COLONY_AGE));
    }

    public boolean isStage(int value) {
        return getColonyAge(value) >= COLONY_AGE;
    }

    private boolean isGrow(Block block,int value) {
        if(block == Blocks.mycelium) return true;
        if(block == Blocks.dirt && value == 2) return true;
        return block == ModBlocks.ORGANIC_COMPOST.get() || block == ModBlocks.RICH_SOIL.get();
    }

    protected Item getMushroom() {
        return Item.getItemFromBlock(mushroomType);
    }
}
