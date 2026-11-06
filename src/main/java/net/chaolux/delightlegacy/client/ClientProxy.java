package net.chaolux.delightlegacy.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.chaolux.delightlegacy.DelightLegacy;
import net.chaolux.delightlegacy.client.gui.CookingPotScreen;
import net.chaolux.delightlegacy.client.renderer.CuttingBoardRenderer;
import net.chaolux.delightlegacy.client.renderer.StoveRenderer;
import net.chaolux.delightlegacy.common.CommonProxy;
import net.chaolux.delightlegacy.common.block.entity.CookingPotBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.CuttingBoardBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.StoveBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.container.CookingPotMenu;
import net.chaolux.delightlegacy.legacy.assets.ClassPathResourceProvider;
import net.chaolux.delightlegacy.legacy.assets.LegacyPackType;
import net.chaolux.delightlegacy.legacy.assets.LegacyResources;
import net.chaolux.delightlegacy.legacy.assets.lang.LegacyLanguageReloadListener;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRegistry;
import net.chaolux.delightlegacy.legacy.assets.textures.LegacyResourceProvider;
import net.chaolux.delightlegacy.registry.ModClientModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent fmlPreInitializationEvent) {
        super.preInit(fmlPreInitializationEvent);
        IResourceManager iResourceManager= Minecraft.getMinecraft().getResourceManager();
        LegacyResources.setProvider(LegacyPackType.ASSETS,new LegacyResourceProvider(iResourceManager,new ClassPathResourceProvider(DelightLegacy.class)));
        if(iResourceManager instanceof IReloadableResourceManager) ((IReloadableResourceManager) iResourceManager).registerReloadListener(new LegacyLanguageReloadListener());
        LegacyModelRegistry.register();
        LegacyModelManager.getInstance().register(iResourceManager);
        ModClientModels.register();
    }

    @Override
    public void init(FMLInitializationEvent fmlPreInitializationEvent) {
        super.init(fmlPreInitializationEvent);
        ClientRegistry.bindTileEntitySpecialRenderer(CuttingBoardBlockEntity.class,new CuttingBoardRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(StoveBlockEntity.class,new StoveRenderer());
    }

    @Override
    public void postInit(FMLPostInitializationEvent fmlPostInitializationEvent) {

    }

    @Override
    public Object getClientGuiElement(int value, EntityPlayer entityPlayer, World world,int x,int y,int z) {
        if(value != 0) return null;
        TileEntity tileEntity=world.getTileEntity(x,y,z);
        if(!(tileEntity instanceof CookingPotBlockEntity)) return null;
        return new CookingPotScreen(new CookingPotMenu(entityPlayer.inventory,(CookingPotBlockEntity)tileEntity));
    }
}
