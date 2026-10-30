package net.chaolux.delightlegacy.client.renderer;

import net.chaolux.delightlegacy.common.block.AbstractStoveBlock;
import net.chaolux.delightlegacy.common.block.entity.StoveBlockEntity;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyBakedModel;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class StoveRenderer extends TileEntitySpecialRenderer {
    private final RenderBlocks renderBlocks=new RenderBlocks();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity,double x,double y,double z,float value) {
        if(!(tileEntity instanceof StoveBlockEntity) || tileEntity.getWorldObj() == null) return;
        StoveBlockEntity stoveBlockEntity=(StoveBlockEntity)tileEntity;
        if(stoveBlockEntity.isEmpty()) return;
        int data= AbstractStoveBlock.getFacing(stoveBlockEntity.getBlockMetadata());
        int metadata=stoveBlockEntity.getWorldObj().getLightBrightnessForSkyBlocks(stoveBlockEntity.xCoord,stoveBlockEntity.yCoord + 1,stoveBlockEntity.zCoord,0);
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
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,metadata & 65535,metadata >> 16 & 65535);
            for (int index=0;index < stoveBlockEntity.getItems();index++) {
                ItemStack itemStack=stoveBlockEntity.getStackInSlot(index);
                if(itemStack == null || itemStack.stackSize <= 0) continue;
                float[] stove=stoveBlockEntity.getValue(index);
                double dx=data == 1 ? stove[1] : data == 2 ? -stove[0] : data == 3 ? -stove[1] : stove[0];
                double dz=data == 1 ? -stove[0] : data == 2 ? -stove[1] : data == 3 ? stove[0] : stove[1];
                LegacyBakedModel legacyBakedModel= LegacyModelManager.getInstance().getItemModel(itemStack);
                boolean model=legacyBakedModel != null ? !legacyBakedModel.isGenerated() : isBlock(itemStack);
                GL11.glPushMatrix();
                try {
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glTranslated(x + 0.5 + dx,y + 1.03,z + 0.5 + dz);
                    GL11.glRotatef(-90.0f * data,0,1,0);
                    if(model) {
                        GL11.glRotatef(180.0f,0,1,0);
                        GL11.glScalef(0.35f,0.35f,0.35f);
                        GL11.glTranslatef(-0.5f,-0.5f,-0.5f);
                    } else {
                        GL11.glRotatef(90.0f,1,0,0);
                        GL11.glScalef(0.36f,0.36f,0.36f);
                        GL11.glRotatef(180.0f,0,0,1);
                        GL11.glTranslatef(-0.5f,-0.5f,0);
                        GL11.glDisable(GL11.GL_LIGHTING);
                    }
                    renderItem(itemStack,legacyBakedModel,model);
                } finally {
                    GL11.glColor4f(1,1,1,1);
                    GL11.glPopMatrix();
                }
            }
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,mouseX,mouseY);
            GL11.glColor4f(1,1,1,1);
            GL11.glPopAttrib();
        }
    }

    private static boolean isBlock(ItemStack itemStack) {
        if(!(itemStack.getItem() instanceof ItemBlock)) return false;
        Block block=Block.getBlockFromItem(itemStack.getItem());
        return block != null && RenderBlocks.renderItemIn3d(block.getRenderType());
    }

    private void renderItem(ItemStack itemStack,LegacyBakedModel legacyBakedModel,boolean value) {
        if(legacyBakedModel != null) {
            LegacyModelRenderer.renderItemModel(itemStack,legacyBakedModel,false);
            return;
        }
        Item item=itemStack.getItem();
        if(value && item instanceof ItemBlock) {
            Block block=Block.getBlockFromItem(item);
            if(block != null) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                renderBlocks.renderBlockAsItem(block,itemStack.getItemDamage(),1.0f);
                return;
            }
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
        int data=item.requiresMultipleRenderPasses() ? item.getRenderPasses(itemStack.getItemDamage()) : 1;
        for(int index=0;index < data;index++) {
            IIcon iIcon=item.getIcon(itemStack,index);
            if(iIcon == null) iIcon=item.getIconFromDamageForRenderPass(itemStack.getItemDamage(),index);
            if(iIcon == null && item instanceof ItemBlock) {
                Block block=Block.getBlockFromItem(item);
                if(block != null) iIcon=block.getIcon(1,itemStack.getItemDamage());
            }
            if(iIcon == null) return;
            int metadata=item.getColorFromItemStack(itemStack,index);
            GL11.glColor4f(((metadata >> 16) & 255) / 255.0f,((metadata >> 8) & 255) / 255.0f,(metadata & 255) / 255.0f,1.0f);
            GL11.glPushMatrix();
            try {
                GL11.glTranslatef(0,0,index * 0.001f);
                ItemRenderer.renderItemIn2D(Tessellator.instance,iIcon.getMaxU(), iIcon.getMinV(),iIcon.getMinU(),iIcon.getMaxV(),iIcon.getIconWidth(),iIcon.getIconHeight(),0.0625f);
            } finally {
                GL11.glPopMatrix();
            }
        }
    }
}
