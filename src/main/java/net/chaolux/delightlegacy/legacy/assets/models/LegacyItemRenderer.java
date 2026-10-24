package net.chaolux.delightlegacy.legacy.assets.models;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class LegacyItemRenderer implements IItemRenderer {
    private final Item item;
    LegacyItemRenderer(Item item) {
        this.item=item;
    }

    @Override
    public boolean handleRenderType(ItemStack itemStack,ItemRenderType itemRenderType) {
        return itemStack != null && itemStack.getItem() == item && itemRenderType != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType itemRenderType,ItemStack itemStack,ItemRendererHelper itemRendererHelper) {
        LegacyBakedModel legacyBakedModel=LegacyModelManager.getInstance().getItemModel(itemStack);
        if(legacyBakedModel == null) return false;
        switch (itemRenderType) {
            case ENTITY: if(itemRendererHelper == ItemRendererHelper.ENTITY_ROTATION || itemRendererHelper == ItemRendererHelper.ENTITY_BOBBING) return true;
            return itemRendererHelper == ItemRendererHelper.BLOCK_3D && !legacyBakedModel.isGenerated();
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON: return itemRendererHelper == ItemRendererHelper.EQUIPPED_BLOCK && !legacyBakedModel.isGenerated();
            case INVENTORY: return itemRendererHelper == ItemRendererHelper.INVENTORY_BLOCK && !legacyBakedModel.isGenerated();
            default: return false;
        }
    }

    @Override
    public void renderItem(ItemRenderType itemRenderType,ItemStack itemStack,Object... objects) {
        LegacyBakedModel legacyBakedModel=LegacyModelManager.getInstance().getItemModel(itemStack);
        if(legacyBakedModel == null) return;
        String string=transform(itemRenderType);
        GL11.glPushMatrix();
        if(legacyBakedModel.isGenerated() && itemRenderType == ItemRenderType.ENTITY) GL11.glTranslatef(-0.5f,-0.25f,0.0f);
        if(legacyBakedModel.isGenerated()) {
            if(itemRenderType == ItemRenderType.INVENTORY) GL11.glScalef(16.0f,16.0f,16.0f);
            LegacyModelDefinition.Transform transform=legacyBakedModel.getTransform(string);
            LegacyModelDefinition.Transform current=LegacyBuildModels.getItemTransform(string,legacyBakedModel.isHandheld());
            LegacyModelRenderer.apllyTransform(transform,current);
        } else {
            LegacyModelDefinition.Transform transform=legacyBakedModel.getTransform(string);
            if(itemStack.getItem() instanceof ItemBlock && transform != LegacyModelDefinition.Transform.TRANSFORM) {
                LegacyModelRenderer.apllyTransform(transform,LegacyBuildModels.getItemTransform(string,false));
            } else {
                LegacyModelRenderer.apply(transform);
            }
        }
        if(itemRenderType == ItemRenderType.INVENTORY && itemStack.getItem() instanceof ItemBlock && !legacyBakedModel.isGenerated()) GL11.glTranslatef(0.0f,-0.0625f,0.0f);
        LegacyModelRenderer.renderItemModel(itemStack,legacyBakedModel,itemRenderType == ItemRenderType.INVENTORY);
        GL11.glPopMatrix();
    }

    private static String transform(ItemRenderType itemRenderType) {
        switch (itemRenderType) {
            case ENTITY: return "ground";
            case EQUIPPED: return "thirdperson_righthand";
            case EQUIPPED_FIRST_PERSON: return "firstperson_righthand";
            case INVENTORY: return "gui";
            default: return "gui";
        }
    }
}
