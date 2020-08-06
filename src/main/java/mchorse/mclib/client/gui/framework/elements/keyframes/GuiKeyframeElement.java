package mchorse.mclib.client.gui.framework.elements.keyframes;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Scale;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.keyframes.Keyframe;
import mchorse.mclib.utils.keyframes.KeyframeEasing;
import mchorse.mclib.utils.keyframes.KeyframeInterpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class GuiKeyframeElement extends GuiElement
{
    public static final Color color = new Color();

    public Consumer<Keyframe> callback;
    public Selection which = Selection.NOT_SELECTED;
    public int duration;

    public boolean sliding;
    public boolean dragging;
    protected boolean moving;
    protected boolean scrolling;
    protected int lastX;
    protected double lastT;

    protected Scale scaleX = new Scale(false);

    public GuiKeyframeElement(Minecraft mc, Consumer<Keyframe> callback)
    {
        super(mc);

        this.callback = callback;
    }

    protected void setKeyframe(Keyframe current)
    {
        if (this.callback != null)
        {
            this.callback.accept(current);
        }
    }

    /* Setters */

    public abstract void setTick(double tick);

    public abstract void setValue(double value);

    public abstract void setInterpolation(KeyframeInterpolation interp);

    public abstract void setEasing(KeyframeEasing easing);

    public void setDuration(long duration)
    {
        this.duration = (int) duration;
    }

    /* Graphing code */

    public abstract void resetView();

    /**
     * Recalculate grid's multipliers
     */
    protected void recalcMultipliers()
    {
        this.scaleX.mult = this.recalcMultiplier(this.scaleX.zoom);
    }

    protected int recalcMultiplier(double zoom)
    {
        int factor = (int) (60F / zoom);

        /* Hardcoded caps */
        if (factor > 10000) factor = 10000;
        else if (factor > 5000) factor = 5000;
        else if (factor > 2500) factor = 2500;
        else if (factor > 1000) factor = 1000;
        else if (factor > 500) factor = 500;
        else if (factor > 250) factor = 250;
        else if (factor > 100) factor = 100;
        else if (factor > 50) factor = 50;
        else if (factor > 25) factor = 25;
        else if (factor > 10) factor = 10;
        else if (factor > 5) factor = 5;

        return factor <= 0 ? 1 : factor;
    }

    public int toGraphX(double tick)
    {
        return (int) (this.scaleX.to(tick)) + this.area.mx();
    }

    public double fromGraphX(int mouseX)
    {
        return this.scaleX.from(mouseX - this.area.mx());
    }

    /* Abstract methods */

    public abstract Keyframe getCurrent();

    public boolean isGrabbing()
    {
        return this.dragging && this.moving && this.which == Selection.NOT_SELECTED;
    }

    public void selectByDuration(long duration)
    {}

    public void doubleClick(int mouseX, int mouseY)
    {
        if (this.which == Selection.NOT_SELECTED)
        {
            this.addCurrent(mouseX, mouseY);
        }
        else if (this.which == Selection.KEYFRAME)
        {
            this.removeCurrent();
        }
    }

    public abstract void addCurrent(int mouseX, int mouseY);

    public abstract void removeCurrent();

    /* Common hooks */

    protected void updateMoved()
    {}

    protected void drawRect(BufferBuilder builder, int x, int y, int offset, int c)
    {
        color.set(c, false);

        builder.pos(x - offset, y + offset, 0.0D).color(color.r, color.g, color.b, 1F).endVertex();
        builder.pos(x + offset, y + offset, 0.0D).color(color.r, color.g, color.b, 1F).endVertex();
        builder.pos(x + offset, y - offset, 0.0D).color(color.r, color.g, color.b, 1F).endVertex();
        builder.pos(x - offset, y - offset, 0.0D).color(color.r, color.g, color.b, 1F).endVertex();
    }

    protected void moveNoKeyframe(GuiContext context, Keyframe frame, double x, double y)
    {}

    protected void drawCursor(GuiContext context)
    {}

    /* Mouse handling */

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        if (super.mouseScrolled(context))
        {
            return true;
        }

        if (this.area.isInside(context.mouseX, context.mouseY) && !this.scrolling)
        {
            int scroll = context.mouseWheel;

            if (!Minecraft.IS_RUNNING_ON_MAC)
            {
                scroll = -scroll;
            }

            this.zoom(scroll);
            this.recalcMultipliers();

            return true;
        }

        return false;
    }

    protected void zoom(int scroll)
    {
        this.scaleX.zoom(Math.copySign(this.getZoomFactor(this.scaleX.zoom), scroll), 0.01F, 50F);
    }

    protected double getZoomFactor(double zoom)
    {
        double factor = 0;

        if (zoom < 0.2F) factor = 0.005F;
        else if (zoom < 1.0F) factor = 0.025F;
        else if (zoom < 2.0F) factor = 0.1F;
        else if (zoom < 15.0F) factor = 0.5F;
        else if (zoom <= 50.0F) factor = 1F;

        return factor;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);

        if (this.which == Selection.KEYFRAME)
        {
            if (this.sliding)
            {
                this.postSlideSort(context);
            }

            if (this.moving)
            {
                this.updateMoved();
            }
        }

        this.resetMouseReleased(context);
    }

    protected void postSlideSort(GuiContext context)
    {}

    protected void resetMouseReleased(GuiContext context)
    {
        this.dragging = false;
        this.moving = false;
        this.scrolling = false;
    }
}