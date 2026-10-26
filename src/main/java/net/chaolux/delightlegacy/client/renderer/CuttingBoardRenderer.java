package net.chaolux.delightlegacy.client.renderer;

import net.chaolux.delightlegacy.common.block.entity.CuttingBoardBlockEntity;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyBakedModel;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderer;
import net.chaolux.delightlegacy.legacy.data.tags.LegacyTags;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CuttingBoardRenderer extends TileEntitySpecialRenderer {
    private final Random random=new Random();
    private final RenderBlocks renderBlocks=new RenderBlocks();
    @Override
    public void renderTileEntityAt(TileEntity tileEntity,double x,double y,double z,float value) {
        if(!(tileEntity instanceof CuttingBoardBlockEntity)) return;
        CuttingBoardBlockEntity cuttingBoardBlockEntity=(CuttingBoardBlockEntity)tileEntity;
        ItemStack itemStack=cuttingBoardBlockEntity.getStoredItem();
        if(itemStack == null || itemStack.stackSize <= 0 || cuttingBoardBlockEntity.getWorldObj() == null) return;
        LegacyBakedModel legacyBakedModel= LegacyModelManager.getInstance().getItemModel(itemStack);
        boolean item=isItem(itemStack,legacyBakedModel);
        boolean flatItem=LegacyTags.Items.isItem("farmersdelight:flat_on_cutting_board",itemStack);
        int data=getModel(itemStack);
        int side=cuttingBoardBlockEntity.getBlockMetadata() & 3;
        random.setSeed(Item.getIdFromItem(itemStack.getItem()) + itemStack.getItemDamage());
        int pack=cuttingBoardBlockEntity.getWorldObj().getLightBrightnessForSkyBlocks(cuttingBoardBlockEntity.xCoord,cuttingBoardBlockEntity.yCoord + 1,cuttingBoardBlockEntity.zCoord,0);
        float mouseX= OpenGlHelper.lastBrightnessX;
        float mouseY=OpenGlHelper.lastBrightnessY;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT);
        try {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER,0.1f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_CULL_FACE);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,pack & 65535,pack >> 16 & 65535);
            for (int index=0;index < data;index++) {
                float floatX=data == 1 ? 0.0f : (random.nextFloat() * 2.0f - 1.0f) * 0.075f;
                float floatZ=data == 1 ? 0.0f : (random.nextFloat() * 2.0f - 1.0f) * 0.075f;
                GL11.glPushMatrix();
                try {
                    if(cuttingBoardBlockEntity.isItemCarvingBoard()) {
                        renderItemCarved(x,y,z,side,itemStack);
                    } else if (item && !flatItem) {
                        renderBlock(x,y,z,side,floatX,index,floatZ);
                    } else {
                        renderItemLayingDown(x,y,z,side,floatX,index,floatZ);
                    }
                    renderItem(itemStack,legacyBakedModel);
                } finally {
                    GL11.glPopMatrix();
                }
            }
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,mouseX,mouseY);
            GL11.glColor4f(1.0f,1.0f,1.0f,1.0f);
            GL11.glPopAttrib();
        }
    }

    private static boolean isItem(ItemStack itemStack,LegacyBakedModel legacyBakedModel) {
        if(legacyBakedModel != null) return !legacyBakedModel.isGenerated();
        if(!(itemStack.getItem() instanceof ItemBlock)) return false;
        Block block=Block.getBlockFromItem(itemStack.getItem());
        return block != null && RenderBlocks.renderItemIn3d(block.getRenderType());
    }

    private static void renderItemLayingDown(double x,double y,double z,int side,float mouseX,int index,float mouseZ) {
        GL11.glTranslated(x + 0.5 + mouseX,y + 0.08 + 0.03 * (index + 1),z + 0.5 + mouseZ);
        GL11.glRotatef(-90.0f * side + 180.0f,0.0f,1.0f,0.0f);
        GL11.glRotatef(90.0f,1.0f,0.0f,0.0f);
        GL11.glScalef(0.6f,0.6f,0.6f);
    }

    private static void renderBlock(double x,double y,double z,int side,float mouseX,int index,float mouseZ) {
        GL11.glTranslated(x + 0.5 + mouseX,y + 0.27 + 0.03 * (index + 1),z + 0.5 + mouseZ);
        GL11.glRotatef(-90.0f * side,0.0f,1.0f,0.0f);
        GL11.glScalef(0.8f,0.8f,0.8f);
    }

    private static void renderItemCarved(double x,double y,double z,int side,ItemStack itemStack) {
        GL11.glTranslated(x + 0.5,y + 0.23,z + 0.5);
        GL11.glRotatef(-90.0f * side + 180.0f,0.0f,1.0f,0.0f);
        float data=itemStack.getItem() instanceof ItemPickaxe || itemStack.getItem() instanceof ItemHoe ? 225.0f : 180.0f;
        GL11.glRotatef(data,0.0f,0.0f,1.0f);
        GL11.glScalef(0.6f,0.6f,0.6f);
    }

    private void renderItem(ItemStack itemStack,LegacyBakedModel legacyBakedModel) {
        GL11.glPushMatrix();
        try {
            if (legacyBakedModel != null) {
                if(!legacyBakedModel.isGenerated()) GL11.glScalef(0.5f,0.5f,0.5f);
                GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
                LegacyModelRenderer.apply(legacyBakedModel.getTransform("fixed"));
                if(legacyBakedModel.isGenerated()) {
                    GL11.glTranslatef(0.0f,0.0f,0.53125f);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_CULL_FACE);
                }
                LegacyModelRenderer.renderItemModel(itemStack, legacyBakedModel, false);
                return;
            }
            if (itemStack.getItem() instanceof ItemBlock) {
                Block block = Block.getBlockFromItem(itemStack.getItem());
                if (block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
                    GL11.glScalef(0.5f,0.5f,0.5f);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                    renderBlocks.renderBlockAsItem(block, itemStack.getItemDamage(), 1.0f);
                    return;
                }
            }
            GL11.glRotatef(180.0f,0.0f,1.0f,0.0f);
            GL11.glTranslatef(-0.5f, -0.5f, 0.03125f);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            Item item=itemStack.getItem();
            Minecraft.getMinecraft().getTextureManager().bindTexture(item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
            int data=item.requiresMultipleRenderPasses() ? item.getRenderPasses(itemStack.getItemDamage()) : 1;
            for (int index=0;index < data;index++) {
                IIcon iIcon=item.getIcon(itemStack,index);
                if(iIcon == null) iIcon=item.getIconFromDamageForRenderPass(itemStack.getItemDamage(),index);
                if(iIcon == null && item instanceof ItemBlock) {
                    Block block=Block.getBlockFromItem(item);
                    if(block != null) iIcon=block.getIcon(1,itemStack.getItemDamage());
                }
                renderIcon(iIcon,item.getColorFromItemStack(itemStack,index),index);
            }
        } finally {
            GL11.glColor4f(1.0f,1.0f,1.0f,1.0f);
            GL11.glPopMatrix();
        }
    }

   private static void renderIcon(IIcon iIcon,int value,int data) {
        if(iIcon == null) return;
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(0.0f,0.0f,data * 0.001f);
            GL11.glColor4f(((value >> 16) & 255) / 255.0f,((value >> 8) & 255) / 255.0f,(value & 255) / 255.0f,1.0f);
            ItemRenderer.renderItemIn2D(Tessellator.instance,iIcon.getMaxU(),iIcon.getMinV(),iIcon.getMinU(),iIcon.getMaxV(),iIcon.getIconWidth(),iIcon.getIconHeight(),0.0625f);
        } finally {
            GL11.glPopMatrix();
        }
   }

    private static int getModel(ItemStack itemStack) {
        return itemStack.stackSize > 1 ? 1 + (int)Math.ceil((float)itemStack.stackSize / Math.max(1,itemStack.getMaxStackSize()) * 4.0f) : 1;
    }

}
