package net.chaolux.delightlegacy.common;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.chaolux.delightlegacy.DelightLegacy;
import net.chaolux.delightlegacy.common.block.entity.CookingPotBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.CuttingBoardBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.StoveBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.container.CookingPotMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int value, EntityPlayer entityPlayer, World world,int x,int y,int z) {
        if(value != 0) return null;
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        return tileEntity instanceof CookingPotBlockEntity ? new CookingPotMenu(entityPlayer.inventory,(CookingPotBlockEntity)tileEntity) : null;
    }

    @Override
    public Object getClientGuiElement(int value,EntityPlayer entityPlayer,World world,int x,int y,int z) {
        return null;
    }

    public void preInit(FMLPreInitializationEvent fmlPreInitializationEvent) {
        GameRegistry.registerTileEntity(CuttingBoardBlockEntity.class,"delightlegacy:cutting_board");
        GameRegistry.registerTileEntity(StoveBlockEntity.class,"delightlegacy:stove");
        GameRegistry.registerTileEntity(CookingPotBlockEntity.class,"delightlegacy:cooking_pot");
    }

    public void init(FMLInitializationEvent fmlInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(DelightLegacy.DELIGHT_LEGACY,this);
    }

    public void postInit(FMLPostInitializationEvent fmlPostInitializationEvent) {

    }
}
