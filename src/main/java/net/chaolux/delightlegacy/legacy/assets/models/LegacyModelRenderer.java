/*
Copyright (c) Forge Development LLC and contributors
SPDX-License-Identifier: LGPL-2.1-only
Modified by Chaolux, 2026
 */
package net.chaolux.delightlegacy.legacy.assets.models;

import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyBlockStateDefinition;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class LegacyModelRenderer {
    private static final int BRIGHTNESS=0x00F000F0;
    public static boolean render(IBlockAccess iBlockAccess, int x, int y, int z, Block block, List<LegacyBlockStateDefinition.Model> modelList,IIcon iIcon) {
        if(modelList == null || modelList.isEmpty()) return false;
        Tessellator tessellator=Tessellator.instance;
        for(LegacyBlockStateDefinition.Model model : modelList) {
            LegacyBakedModel legacyBakedModel=LegacyModelManager.getInstance().getBakedModel(model.getModel());
            renderBake(iBlockAccess,x,y,z,block,legacyBakedModel,model,tessellator,iIcon);
        }
        return true;
    }

    public static void renderInventory(Block block,int data,List<LegacyBlockStateDefinition.Model> modelList) {
        if(modelList == null || modelList.isEmpty()) return;
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator=Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(BRIGHTNESS);
        for (LegacyBlockStateDefinition.Model model : modelList) {
            LegacyBakedModel legacyBakedModel=LegacyModelManager.getInstance().getBakedModel(model.getModel());
            renderBakeItem(null,legacyBakedModel,model,tessellator);
        }
        tessellator.draw();
    }

    public static void renderItemModel(ItemStack itemStack,LegacyBakedModel legacyBakedModel,boolean handheld) {
        if(legacyBakedModel == null) return;
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        if(legacyBakedModel.isGenerated()) {
           if(handheld) {
               renderGeneratedHandheld(itemStack,legacyBakedModel);
           } else {
               renderGenerated(itemStack,legacyBakedModel);
           }
           return;
        }
        Tessellator tessellator=Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(BRIGHTNESS);
        renderBakeItem(itemStack,legacyBakedModel,null,tessellator);
        tessellator.draw();
    }

    public static void apply(LegacyModelDefinition.Transform transform) {
        if(transform == null) return;
        float[] rotate= transform.getRotate();
        float[] translation=transform.getTransform();
        float[] scale=transform.getScale();
        GL11.glTranslatef(translation[0] / 16.0f,translation[1] / 16.0f,translation[2] / 16.0f);
        GL11.glTranslatef(0.5f,0.5f,0.5f);
        GL11.glRotatef(rotate[2],0.0f,0.0f,1.0f);
        GL11.glRotatef(rotate[1],0.0f,1.0f,0.0f);
        GL11.glRotatef(rotate[0],1.0f,0.0f,0.0f);
        GL11.glScalef(scale[0],scale[1],scale[2]);
        GL11.glTranslatef(-0.5f,-0.5f,-0.5f);
    }

    public static void apllyTransform(LegacyModelDefinition.Transform transform,LegacyModelDefinition.Transform current) {
        if(current != null) applyInverse(current);
        if(transform != null) apply(transform);
    }

    private static void applyInverse(LegacyModelDefinition.Transform transform) {
        float[] rotate=transform.getRotate();
        float[] translation=transform.getTransform();
        float[] scale=transform.getScale();
        GL11.glTranslatef(0.5f,0.5f,0.5f);
        GL11.glScalef(inverse(scale[0]),inverse(scale[1]),inverse(scale[2]));
        GL11.glRotatef(-rotate[0],1.0f,0.0f,0.0f);
        GL11.glRotatef(-rotate[1],0.0f,1.0f,0.0f);
        GL11.glRotatef(-rotate[2],0.0f,0.0f,1.0f);
        GL11.glTranslatef(-0.5f,-0.5f,-0.5f);
        GL11.glTranslatef(-translation[0] / 16.0f,-translation[1] / 16.0f,-translation[2] / 16.0f );
    }

    private static void renderBake(IBlockAccess iBlockAccess,int x,int y,int z,Block block,LegacyBakedModel legacyBakedModel,LegacyBlockStateDefinition.Model model,Tessellator tessellator,IIcon iIcon) {
        for(LegacyBakedModel.Quad quad : legacyBakedModel.getQuad()) {
            LegacyDirection legacyDirection=quad.getDirection();
            if(legacyDirection != null) {
                legacyDirection=legacyDirection.rotate(model.getRotateX(),model.getRotateY());
                if(!block.shouldSideBeRendered(iBlockAccess,x + legacyDirection.getOffsetX(),y + legacyDirection.getOffsetY(),z + legacyDirection.getOffsetZ(),legacyDirection.getDirection())) continue;
            }
            LegacyDirection direction=quad.getLegacyDirection().rotate(model.getRotateX(),model.getRotateY());
            int brightness=block.getMixedBrightnessForBlock(iBlockAccess,x + direction.getOffsetX(),y + direction.getOffsetY(),z + direction.getOffsetZ());
            tessellator.setBrightness(brightness);
            setColor(tessellator,iBlockAccess,x,y,z,block,quad,direction);
            setQuad(tessellator,quad,model,x,y,z,iIcon);
        }
    }

    private static void renderBakeItem(ItemStack itemStack,LegacyBakedModel legacyBakedModel,LegacyBlockStateDefinition.Model model,Tessellator tessellator) {
        for (LegacyBakedModel.Quad quad : legacyBakedModel.getQuad()) {
            LegacyDirection legacyDirection=model == null ? quad.getLegacyDirection() : quad.getLegacyDirection().rotate(model.getRotateX(),model.getRotateY());
            setItemColor(tessellator,itemStack,quad,legacyDirection);
            setQuad(tessellator,quad,model,0,0,0,null);
        }
    }

    private static void renderGenerated(ItemStack itemStack,LegacyBakedModel legacyBakedModel) {
        List<ResourceLocation> resourceLocationList=legacyBakedModel.getLayer();
        if(resourceLocationList.isEmpty()) return;
        Tessellator tessellator=Tessellator.instance;
        int index=0;
        for(ResourceLocation resourceLocation : resourceLocationList) {
            IIcon iIcon=LegacyModelManager.getInstance().getTexture().getIcon(resourceLocation);
            int color=itemStack == null ? 0xFFFFFF : itemStack.getItem().getColorFromItemStack(itemStack,index);
            float red=((color >> 16) & 255) / 255.0f;
            float green=((color >> 8) & 255) / 255.0f;
            float blue=(color & 255) / 255.0f;
            GL11.glColor4f(red,green,blue,1.0f);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f,0.0f,index * 0.001f);
            ItemRenderer.renderItemIn2D(tessellator,iIcon.getMaxU(),iIcon.getMinV(),iIcon.getMinU(),iIcon.getMaxV(),iIcon.getIconWidth(),iIcon.getIconHeight(),0.0625f);
            GL11.glPopMatrix();
            index++;
        }
        GL11.glColor4f(1.0f,1.0f,1.0f,1.0f);
    }

    private static void renderGeneratedHandheld(ItemStack itemStack,LegacyBakedModel legacyBakedModel) {
        List<ResourceLocation> resourceLocationList=legacyBakedModel.getLayer();
        if(resourceLocationList.isEmpty()) return;
        Tessellator tessellator=Tessellator.instance;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER,0.1f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
        int index=0;
        for (ResourceLocation resourceLocation : resourceLocationList) {
            IIcon iIcon=LegacyModelManager.getInstance().getTexture().getIcon(resourceLocation);
            int color=itemStack == null ? 0xFFFFFF : itemStack.getItem().getColorFromItemStack(itemStack,index);
            float red=((color >> 16) & 255) / 255.0f;
            float green=((color >> 8) & 255) / 255.0f;
            float blue=(color & 255) / 255.0f;
            GL11.glColor4f(red,green,blue,1.0f);
            double z=index * 0.001;
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0f,0.0f,1.0f);
            tessellator.addVertexWithUV(1.0,1.0f,z,iIcon.getMaxU(),iIcon.getMaxV());
            tessellator.addVertexWithUV(1.0,0.0,z,iIcon.getMaxU(),iIcon.getMinV());
            tessellator.addVertexWithUV(0.0,0.0,z,iIcon.getMinU(),iIcon.getMinV());
            tessellator.addVertexWithUV(0.0,1.0,z,iIcon.getMinU(),iIcon.getMaxV());
            tessellator.draw();
            index++;
        }
        GL11.glColor4f(1.0f,1.0f,1.0f,1.0f);
        GL11.glPopAttrib();
    }

    private static void setQuad(Tessellator tessellator,LegacyBakedModel.Quad quad,LegacyBlockStateDefinition.Model model,int x,int y,int z,IIcon iIcon) {
        IIcon icon=iIcon != null ? iIcon : LegacyModelManager.getInstance().getTexture().getIcon(quad.getLocation());
        LegacyBakedModel.Vertex[] vertices=quad.getVertices();
        int rotate=0;
        if(model != null && model.isLock()) rotate=-(model.getRotateX() + model.getRotateY()) / 90;
        for (int index=0;index < vertices.length;index++) {
            LegacyBakedModel.Vertex vertex=vertices[index];
            float[] floats=model == null ? new float[] {vertex.getX(),vertex.getY(),vertex.getZ()} : setRotate(vertex.getX(),vertex.getY(),vertex.getZ(),model.getRotateX(),model.getRotateY());
            int indexUV=((index + rotate) % 4 + 4) % 4;
            LegacyBakedModel.Vertex vertexUV=vertices[indexUV];
            double u=icon.getInterpolatedU(vertexUV.getU());
            double v=icon.getInterpolatedV(vertexUV.getV());
            tessellator.addVertexWithUV(x + floats[0],y + floats[1],z + floats[2],u,v);
        }
    }

    private static float[] setRotate(float x,float y,float z,int rotateX,int rotateY) {
        x -= 0.5f;
        y -= 0.5f;
        z -= 0.5f;
        int xRotate=normalize(rotateX);
        int yRotate=normalize(rotateY);
        for(int index=0;index < xRotate;index++) {
            float indexY=y;
            y=-z;
            z=indexY;
        }
        for (int index=0;index < yRotate;index++) {
            float indexX=x;
            x=z;
            z=-indexX;
        }
        return new float[] {x + 0.5f,y + 0.5f,z + 0.5f};
    }

    private static int normalize(int degrees) {
        int rotate=(degrees / 90) % 4;
        if(rotate < 0) rotate += 4;
        return rotate;
    }

    private static void setColor(Tessellator tessellator,IBlockAccess iBlockAccess,int x,int y,int z,Block block,LegacyBakedModel.Quad quad,LegacyDirection legacyDirection) {
        int color=quad.getTint() >= 0 ? block.colorMultiplier(iBlockAccess,x,y,z) : 0xFFFFFF;
        float shade=quad.isShade() ? legacyDirection.getShade() : 1.0f;
        float red=((color >> 16) & 255) / 255.0f * shade;
        float green=((color >> 8) & 255) / 255.0f * shade;
        float blue=(color & 255) / 255.0f * shade;
        tessellator.setColorOpaque_F(red,green,blue);
        tessellator.setNormal(legacyDirection.getOffsetX(),legacyDirection.getOffsetY(),legacyDirection.getOffsetZ());
    }

    private static void setItemColor(Tessellator tessellator,ItemStack itemStack,LegacyBakedModel.Quad quad,LegacyDirection legacyDirection) {
        int color=itemStack != null && quad.getTint() >= 0 ? itemStack.getItem().getColorFromItemStack(itemStack,quad.getTint()) : 0xFFFFFF;
        float shade=quad.isShade() ? legacyDirection.getShade() : 1.0f;
        tessellator.setColorOpaque_F(((color >> 16) & 255) / 255.0f * shade,((color >> 8) & 255) / 255.0f * shade,(color & 255) / 255.0f * shade);
        tessellator.setNormal(legacyDirection.getOffsetX(),legacyDirection.getOffsetY(),legacyDirection.getOffsetZ());
    }

    private static float inverse(float value) {
        if(Math.abs(value) < 0.000001f) return 1.0f;
        return 1.0f / value;
    }

}
