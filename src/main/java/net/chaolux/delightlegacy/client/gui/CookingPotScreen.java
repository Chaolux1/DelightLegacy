package net.chaolux.delightlegacy.client.gui;

import net.chaolux.delightlegacy.common.block.entity.CookingPotBlockEntity;
import net.chaolux.delightlegacy.common.block.entity.container.CookingPotMenu;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CookingPotScreen extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE=new ResourceLocation("delightlegacy","textures/gui/cooking_pot.png");
    private final CookingPotMenu cookingPotMenu;
    public CookingPotScreen(CookingPotMenu cookingPotMenu) {
        super(cookingPotMenu);
        this.cookingPotMenu=cookingPotMenu;
        xSize=176;
        ySize=166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x,int y) {
        String string=cookingPotMenu.getCookingPotBlockEntity().hasCustomInventoryName() ? cookingPotMenu.getCookingPotBlockEntity().getInventoryName() : StatCollector.translateToLocal("container.delightlegacy.cooking_pot");
        fontRendererObj.drawString(string,28,6,4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"),8,ySize - 96 + 2,4210752);
        int mouseX=x - guiLeft;
        int mouseY=y - guiTop;
        if(mouseX >= 47 && mouseX < 64 && mouseY >= 55 && mouseY < 70) {
            String value="container.delightlegacy.cooking_pot." + (cookingPotMenu.getCookingPotBlockEntity().isHeated() ? "heated" : "not_heated");
            drawHoveringText(Arrays.asList(StatCollector.translateToLocal(value)),mouseX,mouseY,fontRendererObj);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float value,int x,int y) {
        GL11.glColor4f(1.0f,1.0f,1.0f,1.0f);
        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
        if(cookingPotMenu.getCookingPotBlockEntity().isHeated()) drawTexturedModalRect(guiLeft + 47,guiTop + 55,176,0,17,15);
        int data=cookingPotMenu.getCookTime(24);
        if(data > 0) drawTexturedModalRect(guiLeft + 89,guiTop + 25,176,15,data + 1,17);
    }

    @Override
    protected void renderToolTip(ItemStack itemStack,int x,int y) {
        Slot slot=cookingPotMenu.getSlot(CookingPotBlockEntity.MEAL_DISPLAY_SLOT);
        int mouseX=x-guiLeft;
        int mouseY=y-guiTop;
        if(slot.getHasStack() && slot.getStack() == itemStack && mouseX >= slot.xDisplayPosition && mouseX < slot.xDisplayPosition + 16 && mouseY >= slot.yDisplayPosition && mouseY < slot.yDisplayPosition + 16) {
            List<String> stringList=new ArrayList<String>();
            stringList.add(itemStack.getRarity().rarityColor + itemStack.getDisplayName());
            ItemStack stack=cookingPotMenu.getCookingPotBlockEntity().getContainer();
            if(stack != null && stack.stackSize > 0) stringList.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("container.delightlegacy.cooking_pot.served_on",stack.getDisplayName()));
            drawHoveringText(stringList,x,y,fontRendererObj);
            return;
        }
        super.renderToolTip(itemStack,x,y);
    }
}
