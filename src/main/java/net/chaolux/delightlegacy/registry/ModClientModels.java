package net.chaolux.delightlegacy.registry;

import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyDataStateMapper;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRegistry;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderer;
import net.chaolux.delightlegacy.legacy.registry.LegacyBlockRegister;
import net.chaolux.delightlegacy.legacy.registry.LegacyBlockRegistryObject;
import net.chaolux.delightlegacy.legacy.registry.LegacyRegistryObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ModClientModels {
    public static void register() {
        for (LegacyRegistryObject<? extends Item> legacyRegistryObject : ModItems.ITEMS.getObject()) {
            LegacyModelRegistry.registerItem(legacyRegistryObject.getValue(),legacyRegistryObject.getModel());
        }
        for (LegacyBlockRegistryObject<? extends Block> legacyBlockRegistryObject : ModBlocks.BLOCKS.getObject()) {
            LegacyModelRegistry.registerBlock(legacyBlockRegistryObject.getValue(),legacyBlockRegistryObject.getModel(),legacyBlockRegistryObject.getLegacyBlockStateMapper());
            Block block=legacyBlockRegistryObject.getValue();
            Item item=Item.getItemFromBlock(block);
            if(item != null) LegacyModelRegistry.registerItem(item,ResourceLocations.prefixPath(legacyBlockRegistryObject.getLocation(),"item/"));
        }
    }
}
