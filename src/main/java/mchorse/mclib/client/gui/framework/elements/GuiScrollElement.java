package mchorse.mclib.client.gui.framework.elements;

import org.lwjgl.opengl.GL11;

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
        this.scroll.clamp();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (!this.area.isInside(context.mouseX, context.mouseY))
        {
            return false;
        }

        boolean result = this.scroll.mouseClicked(context.mouseX, context.mouseY);

        context.mouseY += this.scroll.scroll;
        result = result || super.mouseClicked(context);
        context.mouseY -= this.scroll.scroll;

        return result;
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        if (!this.area.isInside(context.mouseX, context.mouseY))
        {
            return false;
        }

        boolean result = this.scroll.mouseScroll(context.mouseX, context.mouseY, context.mouseWheel);

        context.mouseY += this.scroll.scroll;
        result = result || super.mouseScrolled(context);
        context.mouseY -= this.scroll.scroll;

        return result;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        this.scroll.mouseReleased(context.mouseX, context.mouseY);

        context.mouseY += this.scroll.scroll;
        super.mouseReleased(context);
        context.mouseY -= this.scroll.scroll;
    }

    @Override
    public void draw(GuiContext context)
    {
        this.scroll.drag(context.mouseX, context.mouseY);

        context.mouseY += this.scroll.scroll;

        GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, context.screen.width, context.screen.height);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -this.scroll.scroll, 0);

        this.preDraw(context);
        super.draw(context);
        this.postDraw(context);

        GlStateManager.popMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.scroll.drawScrollbar();
        context.mouseY -= this.scroll.scroll;
        context.tooltip.area.y -= this.scroll.scroll;
    }

    protected void preDraw(GuiContext context)
    {}

    protected void postDraw(GuiContext context)
    {}
}