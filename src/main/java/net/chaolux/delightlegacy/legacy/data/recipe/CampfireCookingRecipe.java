package net.chaolux.delightlegacy.legacy.data.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.registry.GameRegistry;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.chaolux.delightlegacy.registry.ModRecipeSerializers;
import net.chaolux.delightlegacy.registry.ModRecipeTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CampfireCookingRecipe implements LegacyRecipe<ItemStack> {
    private final ResourceLocation resourceLocation;
    private final LegacyIngredient legacyIngredient;
    private final ItemStack result;
    private final int cookingTime;
    private final float experience;
    public CampfireCookingRecipe(ResourceLocation resourceLocation,LegacyIngredient legacyIngredient,ItemStack result,int cookingTime,float experience) {
        this.resourceLocation=resourceLocation;
        this.legacyIngredient=legacyIngredient;
        this.result=result.copy();
        this.cookingTime=cookingTime;
        this.experience=experience;
    }

    @Override
    public ResourceLocation getId() {
        return resourceLocation;
    }

    @Override
    public LegacyRecipeType<?> getType() {
        return ModRecipeTypes.CAMPFIRE_COOKING;
    }

    @Override
    public LegacyRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CAMPFIRE_COOKING;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        return legacyIngredient.is(itemStack);
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public float getExperience() {
        return experience;
    }

    public static final class Serializer implements LegacyRecipeSerializer<CampfireCookingRecipe> {
        @Override
        public CampfireCookingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            LegacyIngredient ingredient=LegacyIngredient.fromJson(LegacyRecipeJson.getJson(jsonObject,"ingredient"));
            JsonElement jsonElement=LegacyRecipeJson.getJson(jsonObject,"result");
            ItemStack itemStack;
            if(jsonElement.isJsonObject()) {
                itemStack=LegacyRecipeJson.getStack(jsonElement.getAsJsonObject());
            } else if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                ResourceLocation location= ResourceLocations.parse(jsonElement.getAsString());
                Item item= GameRegistry.findItem(ResourceLocations.namespace(location),ResourceLocations.path(location));
                itemStack=new ItemStack(item);
            } else {
                throw new JsonSyntaxException("Invalid campfire cooking recipe");
            }
            int cookingTime=LegacyRecipeJson.getInt(jsonObject,"cookingtime",600);
            float experience=LegacyRecipeJson.getFloat(jsonObject,"experience",0.0f);
            return new CampfireCookingRecipe(resourceLocation,ingredient,itemStack,cookingTime,experience);
        }
    }
}
