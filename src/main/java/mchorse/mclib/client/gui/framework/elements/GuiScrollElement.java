package mchorse.mclib.client.gui.framework.elements;

import org.lwjgl.opengl.GL11;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.ScrollArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Scroll area GUI class
 * 
 * This bad boy allows to scroll stuff
 */
public abstract class GuiScrollElement extends GuiElement
{
    public ScrollArea scroll = new ScrollArea(0);

    public GuiScrollElement(Minecraft mc)
    {
        super(mc);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.copy(this.area);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        return super.mouseClicked(mouseX, mouseY + this.scroll.scroll, mouseButton) || this.scroll.mouseClicked(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        return super.mouseScrolled(mouseX, mouseY + this.scroll.scroll, scroll) || this.scroll.mouseScroll(mouseX, mouseY, scroll);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY + this.scroll.scroll, state);
        this.scroll.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        this.scroll.drag(mouseX, mouseY);

        GuiScreen screen = this.mc.currentScreen;

        GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -this.scroll.scroll, 0);

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        GlStateManager.popMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.scroll.drawScrollbar();
    }
}