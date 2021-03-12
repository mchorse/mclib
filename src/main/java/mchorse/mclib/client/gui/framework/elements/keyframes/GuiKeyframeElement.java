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

import java.util.List;
import java.util.function.Consumer;

public abstract class GuiKeyframeElement extends GuiElement
{
    public static final Color COLOR = new Color();

    public Consumer<Keyframe> callback;
    public Selection which = Selection.NOT_SELECTED;
    public int duration;

    public double minZoom = 0.01D;
    public double maxZoom = 1000D;

    /**
     * Sliding flag, whether keyframes should be sorted after
     * dragging keyframes around
     */
    public boolean sliding;

    /**
     * Dragging flag, whether dragging got initiated (it might be possible
     * that there are 0 keyframes selected)
     */
    public boolean dragging;

    /**
     * Moving flag, whether the user dragged 3 pixels away from the original
     * place (also could have 0 keyframes selected)
     */
    protected boolean moving;

    /**
     * Scrolling flag, whether the user was navigating by dragging with
     * middle mouse held
     */
    protected boolean scrolling;

    /**
     * Grabbing flag, whether the user selected an area with Shift + click dragging
     * in order to select multiple keyframes
     */
    protected boolean grabbing;

    protected int lastX;
    protected int lastY;
    protected double lastT;
    protected double lastV;

    protected Scale scaleX;

    protected IAxisConverter converter;

    public GuiKeyframeElement(Minecraft mc, Consumer<Keyframe> callback)
    {
        super(mc);

        this.callback = callback;
        this.scaleX = new Scale(this.area, false);
        this.scaleX.anchor(0.5F);
    }

    public void setConverter(IAxisConverter converter)
    {
        this.converter = converter;
    }

    public Scale getScaleX()
    {
        return this.scaleX;
    }

    protected void setKeyframe(Keyframe current)
    {
        if (this.callback != null)
        {
            this.callback.accept(current);
        }
    }

    /* Setters */

    public abstract void setTick(double tick, boolean opposite);

    public abstract void setValue(double value, boolean opposite);

    public abstract void setInterpolation(KeyframeInterpolation interp);

    public abstract void setEasing(KeyframeEasing easing);

    public void setDuration(long duration)
    {
        this.duration = (int) duration;
    }

    /* Graphing code */

    public abstract void resetView();

    public int toGraphX(double tick)
    {
        return (int) (this.scaleX.to(tick));
    }

    public double fromGraphX(int mouseX)
    {
        return this.scaleX.from(mouseX);
    }

    /* Abstract methods */

    public abstract Keyframe getCurrent();

    public abstract List<GuiSheet> getSheets();

    public abstract GuiSheet getSheet(int mouseY);

    public boolean isGrabbing()
    {
        return this.dragging && this.moving && this.grabbing;
    }

    public void selectByDuration(long duration)
    {}

    public abstract void selectAll();

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

    public abstract void removeSelectedKeyframes();

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
                boolean shift = GuiScreen.isShiftKeyDown();

                /* Duplicate the keyframe */
                if (GuiScreen.isAltKeyDown() && !shift && this.which == Selection.KEYFRAME)
                {
                    this.duplicateKeyframe(context, mouseX, mouseY);

                    return false;
                }

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
                this.pickedKeyframe(this.getSelectedCount());
            }
            else if (context.mouseButton == 2)
            {
                this.setupScrolling(context, mouseX, mouseY);
            }
        }

        return false;
    }

    protected void pickedKeyframe(int amount)
    {}

    protected abstract void duplicateKeyframe(GuiContext context, int mouseX, int mouseY);

    protected abstract boolean pickKeyframe(GuiContext context, int mouseX, int mouseY, boolean multi);

    protected void setupScrolling(GuiContext context, int mouseX, int mouseY)
    {
        this.scrolling = true;
        this.lastX = mouseX;
        this.lastY = mouseY;
        this.lastT = this.scaleX.getShift();
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

            return true;
        }

        return false;
    }

    protected void zoom(int scroll)
    {
        this.scaleX.zoom(Math.copySign(this.scaleX.getZoomFactor(), scroll), this.minZoom, this.maxZoom);
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
        int mult = this.scaleX.getMult();
        int hx = this.duration / mult;
        int ht = (int) this.fromGraphX(this.area.x);

        for (int j = Math.max(ht / mult, 0); j <= hx; j++)
        {
            int x = this.toGraphX(j * mult);

            if (x >= this.area.ex())
            {
                break;
            }

            String label = this.converter == null ? String.valueOf(j * mult) : this.converter.format(j * mult);

            Gui.drawRect(x, this.area.y, x + 1, this.area.ey(), 0x44ffffff);
            this.font.drawString(label, x + 4, this.area.y + 4, 0xffffff);
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
            this.keepMoving();
        }
    }

    protected void keepMoving()
    {}

    protected void scrolling(int mouseX, int mouseY)
    {
        this.scaleX.setShift(-(mouseX - this.lastX) / this.scaleX.getZoom() + this.lastT);
    }

    protected Keyframe moving(GuiContext context, int mouseX, int mouseY)
    {
        return null;
    }
}