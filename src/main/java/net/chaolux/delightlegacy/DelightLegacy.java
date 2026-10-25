package net.chaolux.delightlegacy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.chaolux.delightlegacy.common.CommonProxy;
import net.chaolux.delightlegacy.common.event.CommonEvents;
import net.chaolux.delightlegacy.common.tag.ModTags;
import net.chaolux.delightlegacy.legacy.assets.LegacyResources;
import net.chaolux.delightlegacy.legacy.assets.lang.LegacyLanguageLoader;
import net.chaolux.delightlegacy.legacy.data.recipe.LegacyRecipeManager;
import net.chaolux.delightlegacy.legacy.data.tags.LegacyTagManager;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.chaolux.delightlegacy.registry.ModItems;
import net.chaolux.delightlegacy.registry.ModRecipeSerializers;
import net.chaolux.delightlegacy.registry.ModRecipeTypes;
import net.minecraft.util.CombatEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = DelightLegacy.MOD_ID, name = DelightLegacy.NAME, version = Tags.VERSION)
public class DelightLegacy {

    public static final String MOD_ID="delightlegacy";
    public static final String MODERN_MOD_ID="farmersdelight";
    public static final String NAME="Delight Legacy";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @SidedProxy(clientSide = "net.chaolux.delightlegacy.client.ClientProxy",serverSide = "net.chaolux.delightlegacy.common.CommonProxy")
    public static CommonProxy COMMON_PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent fmlPreInitializationEvent) {
        LegacyResources.initialize(DelightLegacy.class);
        ModBlocks.register();
        ModItems.register();
        ModTags.register();
        ModRecipeTypes.resister();
        ModRecipeSerializers.register();
        COMMON_PROXY.preInit(fmlPreInitializationEvent);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent ev) {
        COMMON_PROXY.init(ev);
        CommonEvents.register();

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent fmlPostInitializationEvent) {
        LegacyTagManager.getInstance().reload();
        LegacyRecipeManager.getInstance().reload();
        COMMON_PROXY.postInit(fmlPostInitializationEvent);

    }
}
