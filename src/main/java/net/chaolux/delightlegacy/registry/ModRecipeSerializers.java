package net.chaolux.delightlegacy.registry;

import net.chaolux.delightlegacy.common.crafting.CuttingBoardRecipe;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.chaolux.delightlegacy.legacy.data.recipe.*;

public class ModRecipeSerializers {
    public static LegacyRecipeSerializer<ShapedRecipe> SHAPED_CRAFTING;
    public static LegacyRecipeSerializer<ShapelessRecipe> SHAPELESS_CRAFTING;
    public static LegacyRecipeSerializer<CuttingBoardRecipe> CUTTING;
    public static LegacyRecipeSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING;

    public static void register() {
        SHAPED_CRAFTING=LegacyRecipeRegistry.registerSerializer(ResourceLocations.locations("crafting_shaped"),new ShapedRecipe.Serializer());
        SHAPELESS_CRAFTING=LegacyRecipeRegistry.registerSerializer(ResourceLocations.locations("crafting_shapeless"),new ShapelessRecipe.Serializer());
        CUTTING= LegacyRecipeRegistry.registerSerializer(ResourceLocations.resourceLocations("cutting"),new CuttingBoardRecipe.Serializer());
        CAMPFIRE_COOKING= LegacyRecipeRegistry.registerSerializer(ResourceLocations.locations("campfire_cooking"),new CampfireCookingRecipe.Serializer());
    }
}
