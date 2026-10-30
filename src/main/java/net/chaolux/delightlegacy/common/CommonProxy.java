package net.chaolux.delightlegacy.common;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.chaolux.delightlegacy.common.block.entity.CuttingBoardBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.StoveBlockEntity;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent fmlPreInitializationEvent) {
        GameRegistry.registerTileEntity(CuttingBoardBlockEntity.class,"delightlegacy:cutting_board");
        GameRegistry.registerTileEntity(StoveBlockEntity.class,"delightlegacy:stove");
    }

    public void init(FMLInitializationEvent fmlInitializationEvent) {

    }

    public void postInit(FMLPostInitializationEvent fmlPostInitializationEvent) {

    }
}
