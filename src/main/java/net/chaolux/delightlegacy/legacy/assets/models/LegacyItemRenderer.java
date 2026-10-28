package net.chaolux.delightlegacy.legacy.assets.models;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.Random;

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
        try {
            if (itemRenderType == ItemRenderType.ENTITY) {
                renderGround(itemStack, legacyBakedModel);
                return;
            }
            if (legacyBakedModel.isGenerated()) {
                if (itemRenderType == ItemRenderType.INVENTORY) GL11.glScalef(16.0f, 16.0f, 16.0f);
                LegacyModelDefinition.Transform transform = legacyBakedModel.getTransform(string);
                LegacyModelDefinition.Transform current = LegacyBuildModels.getItemTransform(string, legacyBakedModel.isHandheld());
                LegacyModelRenderer.apllyTransform(transform, current);
            } else {
                LegacyModelDefinition.Transform transform = legacyBakedModel.getTransform(string);
                if (itemStack.getItem() instanceof ItemBlock && transform != LegacyModelDefinition.Transform.TRANSFORM) {
                    LegacyModelRenderer.apllyTransform(transform, LegacyBuildModels.getItemTransform(string, false));
                } else {
                    LegacyModelRenderer.apply(transform);
                }
            }
            if (itemRenderType == ItemRenderType.INVENTORY && itemStack.getItem() instanceof ItemBlock && !legacyBakedModel.isGenerated())
                GL11.glTranslatef(0.0f, -0.0625f, 0.0f);
            LegacyModelRenderer.renderItemModel(itemStack, legacyBakedModel, itemRenderType == ItemRenderType.INVENTORY);
        } finally {
            GL11.glPopMatrix();
        }
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

    private static void renderGround(ItemStack itemStack,LegacyBakedModel legacyBakedModel) {
        float value=0.5f;
        if(!legacyBakedModel.isGenerated() && itemStack.getItem() instanceof ItemBlock) {
            Block block=Block.getBlockFromItem(itemStack.getItem());
            if(block != null) {
                int data=block.getRenderType();
                if(data != 1 && data != 19 && data != 12 && data != 2) value=0.25f;
            }
            GL11.glScalef(0.25f / value,0.25f / value,0.25f / value);
            GL11.glTranslatef(-0.5f,-0.5f,-0.5f);
            LegacyModelRenderer.renderItemModel(itemStack,legacyBakedModel,false);
            return;
        }
        LegacyModelDefinition.Transform transform=legacyBakedModel.getTransform("ground");
        if(!legacyBakedModel.isGenerated() && itemStack.getItem() instanceof ItemBlock && transform == LegacyModelDefinition.Transform.TRANSFORM) transform=new LegacyModelDefinition.Transform(new float[] {0.0f,0.0f,0.0f},new float[] {0.0f,3.0f,0.0f},new float[] {0.25f,0.25f,0.25f});
        float[] values= transform.getTransform();
        float[] item=transform.getScale();
        LegacyModelDefinition.Transform transforms=new LegacyModelDefinition.Transform(transform.getRotate(),new float[] {values[0] / value,values[1] / value,values[2] / value},new float[] {item[0] / value, item[1] / value,item[2] / value});
        GL11.glTranslatef(-0.5f,-0.5f,-0.5f);
        LegacyModelRenderer.apply(transforms);
        if(legacyBakedModel.isGenerated()) {
            GL11.glTranslatef(0.0f, 0.0f, 0.5f + 0.0625f / 2.0f);
            int data = getDrop(itemStack);
            float metadata = 0.0625f + 0.02f;
            Random random = new Random(187);
            GL11.glPushMatrix();
            try {
                GL11.glTranslatef(0.0f, 0.0f, -metadata * data / 2.0f);
                for (int index = 0; index < data; index++) {
                    if (index > 0) {
                        float x = (random.nextFloat() * 2.0f - 1.0f) * 0.3f / 0.5f;
                        float y = (random.nextFloat() * 2.0f - 1.0f) * 0.3f / 0.5f;
                        random.nextFloat();
                        GL11.glTranslatef(x, y, metadata);
                    } else {
                        GL11.glTranslatef(0.0f, 0.0f, metadata);
                    }
                    LegacyModelRenderer.renderItemModel(itemStack, legacyBakedModel, false);
                }
            } finally {
                GL11.glPopMatrix();
            }
        }
    }

    private static int getDrop(ItemStack itemStack) {
        int value=itemStack.stackSize;
        return value < 2 ? 1 : value < 16 ? 2 : value < 32 ? 3 : 4;
    }
}
