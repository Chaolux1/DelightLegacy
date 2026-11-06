package net.chaolux.delightlegacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.DelightLegacy;
import net.chaolux.delightlegacy.common.block.entity.CabinetBlockEntity;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyDataStateMapper;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderID;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CabinetBlock extends BlockContainer {
    public static final int FACING=3;
    public static final int OPEN=4;
    public static final LegacyDataStateMapper STATE_MAPPER=variant();
    public CabinetBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties.getMaterial());
        legacyBlockProperties.from(this);
    }

    @Override
    public TileEntity createNewTileEntity(World world,int value) {
        return new CabinetBlockEntity();
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        int value=(MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0f / 360.0f + 0.5)) & FACING;
        world.setBlockMetadataWithNotify(x,y,z,value,3);
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(tileEntity instanceof CabinetBlockEntity && itemStack != null && itemStack.hasDisplayName()) ((CabinetBlockEntity)tileEntity).setCustomName(itemStack.getDisplayName());
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer,int value,float mouseX,float mouseY,float mouseZ) {
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(!(tileEntity instanceof CabinetBlockEntity)) return false;
        if(!world.isRemote) entityPlayer.openGui(DelightLegacy.DELIGHT_LEGACY,1,world,x,y,z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block,int value) {
        if(!world.isRemote) {
            TileEntity tileEntity=world.getTileEntity(x,y,z);
            if(tileEntity instanceof CabinetBlockEntity) ((CabinetBlockEntity)tileEntity).dropItems();
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
        return tileEntity instanceof CabinetBlockEntity ? Container.calcRedstoneFromInventory((CabinetBlockEntity)tileEntity) : 0;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    private static LegacyDataStateMapper variant() {
        LegacyDataStateMapper.Builder builder=LegacyDataStateMapper.builder();
        String[] strings={"north","east","south","west"};
        for (int index=0;index < 8;index++) {
            builder.put(index,"facing",strings[index & FACING]);
            builder.put(index,"open",String.valueOf((index & OPEN) != 0));
        }
        return builder.build();
    }
}
