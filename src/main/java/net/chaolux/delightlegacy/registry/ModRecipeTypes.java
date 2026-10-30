package net.chaolux.delightlegacy.registry;

import net.chaolux.delightlegacy.common.crafting.CuttingBoardRecipe;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.chaolux.delightlegacy.legacy.data.recipe.CampfireCookingRecipe;
import net.chaolux.delightlegacy.legacy.data.recipe.LegacyCraftingRecipe;
import net.chaolux.delightlegacy.legacy.data.recipe.LegacyRecipeRegistry;
import net.chaolux.delightlegacy.legacy.data.recipe.LegacyRecipeType;

public class ModRecipeTypes {
    public static LegacyRecipeType<LegacyCraftingRecipe> CRAFTING;
    public static LegacyRecipeType<CuttingBoardRecipe> CUTTING;
    public static LegacyRecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING;

    public static void resister() {
        CRAFTING=LegacyRecipeRegistry.registerType(ResourceLocations.locations("crafting"));
        CUTTING= LegacyRecipeRegistry.registerType(ResourceLocations.resourceLocations("cutting"));
        CAMPFIRE_COOKING=LegacyRecipeRegistry.registerType(ResourceLocations.locations("campfire_cooking"));
    }
}
