package net.chaolux.delightlegacy.legacy.assets.blockstates;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelRenderer;
import net.chaolux.delightlegacy.legacy.common.block.LegacyLiquidBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class LegacyBlockRenderer implements ISimpleBlockRenderingHandler {
    private final int value;
    public LegacyBlockRenderer(int value) {
        this.value=value;
    }

    @Override
    public void renderInventoryBlock(Block block, int data, int value, RenderBlocks renderBlocks) {
        List<LegacyBlockStateDefinition.Model> modelList= LegacyModelManager.getInstance().getInventoryModel(block,data);
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5f,-0.5f,-0.5f);
        LegacyModelRenderer.renderInventory(block,data,modelList);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess iBlockAccess,int x,int y,int z,Block block,int value,RenderBlocks renderBlocks) {
        boolean render=false;
        if(block instanceof LegacyLiquidBlock) {
            Block liquidBlock=((LegacyLiquidBlock) block).getLiquid();
            if(liquidBlock != null) {
                render |= renderBlocks.renderBlockLiquid(liquidBlock,x,y,z);
            }
        }
        List<LegacyBlockStateDefinition.Model> modelList=LegacyModelManager.getInstance().getModel(iBlockAccess,x,y,z,block);
        render |= LegacyModelRenderer.render(iBlockAccess,x,y,z,block,modelList);
        return render;
    }

    @Override
    public boolean shouldRender3DInInventory(int value) {
        return true;
    }

    @Override
    public int getRenderId() {
        return value;
    }
}
