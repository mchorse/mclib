package mchorse.mclib.client.gui.framework.elements.keyframes;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Scale;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.keyframes.Keyframe;
import mchorse.mclib.utils.keyframes.KeyframeEasing;
import mchorse.mclib.utils.keyframes.KeyframeInterpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

public abstract class GuiKeyframeElement extends GuiElement
{
    public static final Color COLOR = new Color();

    public Consumer<Keyframe> callback;
    public Selection which = Selection.NOT_SELECTED;
    public int duration;

    public boolean sliding;
    public boolean dragging;
    protected boolean moving;
    protected boolean scrolling;
    protected boolean grabbing;
    protected int lastX;
    protected int lastY;
    protected double lastT;
    protected double lastV;

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
        return this.dragging && this.moving && this.grabbing;
    }

    public void selectByDuration(long duration)
    {}

    public abstract int getSelectedCount();

    public boolean isMultipleSelected()
    {
        return this.getSelectedCount() > 1;
    }

    public boolean hasSelected()
    {
        return this.getSelectedCount() > 0;
    }

    public abstract void clearSelection();

    public void doubleClick(int mouseX, int mouseY)
    {
        if (this.which == Selection.NOT_SELECTED)
        {
            this.addCurrent(mouseX, mouseY);
        }
        else if (this.which == Selection.KEYFRAME && !this.isMultipleSelected())
        {
            this.removeCurrent();
        }
    }

    public abstract void addCurrent(int mouseX, int mouseY);

    public abstract void removeCurrent();

    /* Common hooks */

    protected void updateMoved()
    {}

    protected void moveNoKeyframe(GuiContext context, Keyframe frame, double x, double y)
    {}

    protected void drawCursor(GuiContext context)
    {}

    /* Mouse input handling */

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        /* Select current point with a mouse click */
        if (this.area.isInside(mouseX, mouseY))
        {
            if (context.mouseButton == 0)
            {
                /* Duplicate the keyframe */
                if (GuiScreen.isAltKeyDown() && this.which == Selection.KEYFRAME)
                {
                    Keyframe frame = this.getCurrent();

                    if (frame != null)
                    {
                        this.duplicateKeyframe(frame, context, mouseX, mouseY);
                    }

                    return false;
                }

                boolean shift = GuiScreen.isShiftKeyDown();
                this.lastX = mouseX;
                this.lastY = mouseY;

                if (shift)
                {
                    this.grabbing = true;
                }

                if (!this.pickKeyframe(context, mouseX, mouseY, shift) && !shift)
                {
                    this.clearSelection();
                    this.setKeyframe(null);
                }

                this.dragging = true;
            }
            else if (context.mouseButton == 2)
            {
                this.setupScrolling(context, mouseX, mouseY);
            }
        }

        return false;
    }

    protected void duplicateKeyframe(Keyframe frame, GuiContext context, int mouseX, int mouseY)
    {}

    protected abstract boolean pickKeyframe(GuiContext context, int mouseX, int mouseY, boolean multi);

    protected void setupScrolling(GuiContext context, int mouseX, int mouseY)
    {
        this.scrolling = true;
        this.lastX = mouseX;
        this.lastY = mouseY;
        this.lastT = this.scaleX.shift;
    }

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
        this.grabbing = false;
        this.dragging = false;
        this.moving = false;
        this.scrolling = false;
    }

    /* Rendering */

    @Override
    public void draw(GuiContext context)
    {
        this.handleMouse(context, context.mouseX, context.mouseY);
        this.drawBackground(context);

        GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);

        this.drawGrid(context);
        this.drawCursor(context);

        /* Draw graph of the keyframe channel */
        GlStateManager.glLineWidth(Minecraft.getMinecraft().gameSettings.guiScale * 1.5F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        this.drawGraph(context, context.mouseX, context.mouseY);

        /* Draw selection box */
        if (this.isGrabbing())
        {
            Gui.drawRect(this.lastX, this.lastY, context.mouseX, context.mouseY, 0x440088ff);
        }

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();

        GuiDraw.unscissor(context);

        super.draw(context);
    }

    protected void drawBackground(GuiContext context)
    {
        this.area.draw(0x88000000);

        if (this.duration > 0)
        {
            int leftBorder = this.toGraphX(0);
            int rightBorder = this.toGraphX(this.duration);

            if (leftBorder > this.area.x) Gui.drawRect(this.area.x, this.area.y, leftBorder, this.area.y + this.area.h, 0x88000000);
            if (rightBorder < this.area.ex()) Gui.drawRect(rightBorder, this.area.y, this.area.ex() , this.area.y + this.area.h, 0x88000000);
        }
    }

    protected void drawGrid(GuiContext context)
    {
        /* Draw scaling grid */
        int hx = this.duration / this.scaleX.mult;
        int ht = (int) this.fromGraphX(this.area.x);

        for (int j = Math.max(ht / this.scaleX.mult, 0); j <= hx; j++)
        {
            int x = this.toGraphX(j * this.scaleX.mult);

            if (x >= this.area.ex())
            {
                break;
            }

            Gui.drawRect(x, this.area.y, x + 1, this.area.ey(), 0x44ffffff);
            this.font.drawString(String.valueOf(j * this.scaleX.mult), x + 4, this.area.y + 4, 0xffffff);
        }
    }

    protected abstract void drawGraph(GuiContext context, int mouseX, int mouseY);

    protected void drawRect(BufferBuilder builder, int x, int y, int offset, int c)
    {
        COLOR.set(c, false);

        builder.pos(x - offset, y + offset, 0.0D).color(COLOR.r, COLOR.g, COLOR.b, 1F).endVertex();
        builder.pos(x + offset, y + offset, 0.0D).color(COLOR.r, COLOR.g, COLOR.b, 1F).endVertex();
        builder.pos(x + offset, y - offset, 0.0D).color(COLOR.r, COLOR.g, COLOR.b, 1F).endVertex();
        builder.pos(x - offset, y - offset, 0.0D).color(COLOR.r, COLOR.g, COLOR.b, 1F).endVertex();
    }

    /* Handling dragging */

    protected void handleMouse(GuiContext context, int mouseX, int mouseY)
    {
        if (this.dragging && !this.moving && (Math.abs(this.lastX - mouseX) > 3 || Math.abs(this.lastY - mouseY) > 3))
        {
            this.moving = true;
            this.sliding = true;
        }

        if (this.scrolling)
        {
            this.scrolling(mouseX, mouseY);
        }
        /* Move the current keyframe */
        else if (this.moving && !this.grabbing)
        {
            this.setKeyframe(this.moving(context, mouseX, mouseY));
        }
    }

    protected void scrolling(int mouseX, int mouseY)
    {
        this.scaleX.shift = -(mouseX - this.lastX) / this.scaleX.zoom + this.lastT;
    }

    protected Keyframe moving(GuiContext context, int mouseX, int mouseY)
    {
        return null;
    }
}