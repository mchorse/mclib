package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;

import mchorse.mclib.client.gui.utils.ScrollArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Scroll area GUI class
 * 
 * This bad boy allows to scroll stuff
 */
public class GuiScrollElement extends GuiElement
{
    public ScrollArea scroll = new ScrollArea(0);

    public GuiScrollElement(Minecraft mc)
    {
        this(mc, ScrollArea.ScrollDirection.VERTICAL);
    }

    public GuiScrollElement(Minecraft mc, ScrollArea.ScrollDirection direction)
    {
        super(mc);

        this.scroll.direction = direction;
        this.scroll.scrollSpeed = 20;
    }

    private void apply(GuiContext context)
    {
        if (this.scroll.direction == ScrollArea.ScrollDirection.VERTICAL)
        {
            context.mouseY += this.scroll.scroll;
            context.shiftY += this.scroll.scroll;
        }
        else
        {
            context.mouseX += this.scroll.scroll;
            context.shiftX += this.scroll.scroll;
        }
    }

    private void unapply(GuiContext context)
    {
        if (this.scroll.direction == ScrollArea.ScrollDirection.VERTICAL)
        {
            context.mouseY -= this.scroll.scroll;
            context.shiftY -= this.scroll.scroll;
        }
        else
        {
            context.mouseX -= this.scroll.scroll;
            context.shiftX -= this.scroll.scroll;
        }
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

        this.apply(context);
        result = result || super.mouseClicked(context);
        this.unapply(context);

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

        this.apply(context);
        result = result || super.mouseScrolled(context);
        this.unapply(context);

        return result;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        this.scroll.mouseReleased(context.mouseX, context.mouseY);

        this.apply(context);
        super.mouseReleased(context);
        this.unapply(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.scroll.drag(context.mouseX, context.mouseY);

        GuiDraw.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, context);
        GlStateManager.pushMatrix();

        if (this.scroll.direction == ScrollArea.ScrollDirection.VERTICAL)
        {
            GlStateManager.translate(0, -this.scroll.scroll, 0);
        }
        else
        {
            GlStateManager.translate(-this.scroll.scroll, 0, 0);
        }

        this.apply(context);

        this.preDraw(context);
        super.draw(context);
        this.postDraw(context);

        GlStateManager.popMatrix();
        GuiDraw.unscissor();

        this.scroll.drawScrollbar();
        this.unapply(context);

        if (this.scroll.direction == ScrollArea.ScrollDirection.VERTICAL)
        {
            context.tooltip.area.y -= this.scroll.scroll;
        }
        else
        {
            context.tooltip.area.x -= this.scroll.scroll;
        }
    }

    protected void preDraw(GuiContext context)
    {}

    protected void postDraw(GuiContext context)
    {}
}