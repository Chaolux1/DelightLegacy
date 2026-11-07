package net.chaolux.delightlegacy.common;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.chaolux.delightlegacy.DelightLegacy;
import net.chaolux.delightlegacy.common.block.entity.*;
import net.chaolux.delightlegacy.common.block.entity.container.CookingPotMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int value, EntityPlayer entityPlayer, World world,int x,int y,int z) {
        if(value != 0 && value != 1 && value != 2) return null;
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(value == 1) return tileEntity instanceof CabinetBlockEntity ? new ContainerChest(entityPlayer.inventory,(CabinetBlockEntity)tileEntity) : null;
        if(value == 2) return tileEntity instanceof BasketBlockEntity ? new ContainerChest(entityPlayer.inventory,(BasketBlockEntity)tileEntity) : null;
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
        GameRegistry.registerTileEntity(CabinetBlockEntity.class,"delightlegacy:cabinet");
        GameRegistry.registerTileEntity(BasketBlockEntity.class,"delightlegacy:basket");

    }

    public void init(FMLInitializationEvent fmlInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(DelightLegacy.DELIGHT_LEGACY,this);
    }

    public void postInit(FMLPostInitializationEvent fmlPostInitializationEvent) {

    }
}
