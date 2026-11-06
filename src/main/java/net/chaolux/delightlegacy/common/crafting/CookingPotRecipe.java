package net.chaolux.delightlegacy.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.chaolux.delightlegacy.legacy.data.recipe.*;
import net.chaolux.delightlegacy.registry.ModRecipeSerializers;
import net.chaolux.delightlegacy.registry.ModRecipeTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CookingPotRecipe implements LegacyRecipe<ItemStack[]> {
    public static final int INPUT_SLOTS = 6;
    private final ResourceLocation id;
    private final String group;
    private final String tab;
    private final List<LegacyIngredient> inputItems;
    private final ItemStack output;
    private final ItemStack container;
    private final float experience;
    private final int cookTime;
    public CookingPotRecipe(ResourceLocation id,String group,String tab,List<LegacyIngredient> inputItems,ItemStack output,ItemStack container,float experience,int cookTime) {
        this.id=id;
        this.group=group == null ? "" : group;
        this.tab=tab;
        this.inputItems= Collections.unmodifiableList(new ArrayList<LegacyIngredient>(inputItems));
        this.output=output.copy();
        if(container != null && container.stackSize > 0) {
            this.container=container.copy();
        } else {
            Item item=output.getItem();
            this.container=item != null && item.hasContainerItem() && item.getContainerItem() != null ? new ItemStack(item.getContainerItem()) : null;
        }
        if(this.container != null) this.container.stackSize=1;
        this.experience=experience;
        this.cookTime=cookTime;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public LegacyRecipeType<?> getType() {
        return ModRecipeTypes.COOKING;
    }

    @Override
    public LegacyRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.COOKING;
    }

    @Override
    public boolean matches(ItemStack[] itemStacks) {
        if(itemStacks == null || itemStacks.length < INPUT_SLOTS) return false;
        List<ItemStack> itemStackList=new ArrayList<ItemStack>(INPUT_SLOTS);
        for(int index=0;index < INPUT_SLOTS;index++) {
            ItemStack itemStack=itemStacks[index];
            if(itemStack != null && itemStack.stackSize > 0) itemStackList.add(itemStack);
        }
        if(itemStackList.size() != inputItems.size()) return false;
        return matches(itemStackList,new boolean[itemStackList.size()],0);
    }

    private boolean matches(List<ItemStack> itemStackList,boolean[] value,int data) {
        if(data >= inputItems.size()) return true;
        LegacyIngredient legacyIngredient=inputItems.get(data);
        for(int index=0;index < itemStackList.size();index++) {
            if(value[index] || !legacyIngredient.is(itemStackList.get(index))) continue;
            value[index]=true;
            if(matches(itemStackList,value,data + 1)) return true;
            value[index]=false;
        }
        return false;
    }

    public ItemStack getResultItem() {
        return output.copy();
    }

    public ItemStack getOutputContainer() {
        return container == null ? null : container.copy();
    }

    public List<LegacyIngredient> getIngredients() {
        return inputItems;
    }

    public String getGroup() {
        return group;
    }

    public String getRecipeBookTab() {
        return tab;
    }

    public float getExperience() {
        return experience;
    }

    public int getCookTime() {
        return cookTime;
    }

    public static final class Serializer implements LegacyRecipeSerializer<CookingPotRecipe> {
        @Override
        public CookingPotRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            JsonArray jsonArray= LegacyRecipeJson.getArray(jsonObject,"ingredients");
            if(jsonArray.size() < 1 || jsonArray.size() > INPUT_SLOTS) throw new JsonSyntaxException("Too many ingredients for cooking recipe! The max is 6");
            List<LegacyIngredient> legacyIngredientList=new ArrayList<LegacyIngredient>();
            for (JsonElement jsonElement : jsonArray) {
                LegacyIngredient legacyIngredient=LegacyIngredient.fromJson(jsonElement);
                if(legacyIngredient.isEmpty()) throw new JsonSyntaxException("No ingredients for cooking recipe");
                legacyIngredientList.add(legacyIngredient);
            }
            ItemStack itemStack=LegacyRecipeJson.getStack(LegacyRecipeJson.getObject(jsonObject,"result"));
            ItemStack stack=jsonObject.has("container") ? LegacyRecipeJson.getStack(LegacyRecipeJson.getObject(jsonObject,"container")) : null;
            String string=LegacyRecipeJson.getString(jsonObject,"group","");
            String strings=LegacyRecipeJson.getString(jsonObject,"recipe_book_tab","");
            float experience=LegacyRecipeJson.getFloat(jsonObject,"experience",0);
            int cookTime=LegacyRecipeJson.getInt(jsonObject,"cookingtime",200);
            return new CookingPotRecipe(resourceLocation,string,strings,legacyIngredientList,itemStack,stack,experience,cookTime);
        }
    }
}
