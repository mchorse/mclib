package mchorse.mclib.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Utility class for boxes
 *
 * Used in GUI for rendering and locating cursor inside of the box purposes.
 */
public class Area
{
    /**
     * Shared area which could be used for calculations without creating new
     * instances
     */
    public static final Area SHARED = new Area();

    /**
     * X position coordinate of the box
     */
    public int x;

    /**
     * Y position coordinate of the box
     */
    public int y;

    /**
     * Width of the box
     */
    public int w;

    /**
     * Height of the box
     */
    public int h;

    public Area()
    {}

    public Area(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @SideOnly(Side.CLIENT)
    public boolean isInside(GuiContext context)
    {
        return this.isInside(context.mouseX, context.mouseY);
    }

    /**
     * Check whether given position is inside of the rect
     */
    public boolean isInside(int x, int y)
    {
        return x >= this.x && x < this.x + this.w && y >= this.y && y < this.y + this.h;
    }

    /**
     * Check whether given rect intersects this rect
     */
    public boolean intersects(Area area)
    {
        return this.x < area.x + area.w && this.y < area.y + area.h
            && area.x < this.x + this.w && area.y < this.y + this.h;
    }

    /**
     * Set all values
     */
    public void set(int x, int y, int w, int h)
    {
        this.setPos(x, y);
        this.setSize(w, h);
    }

    /**
     * Set the position
     */
    public void setPos(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the size
     */
    public void setSize(int w, int h)
    {
        this.w = w;
        this.h = h;
    }

    /**
     * Copy properties from other area 
     */
    public void copy(Area area)
    {
        this.x = area.x;
        this.y = area.y;
        this.w = area.w;
        this.h = area.h;
    }

    /**
     * Calculate X based on anchor value
     */
    public int x(float anchor)
    {
        return this.x + (int) (this.w * anchor);
    }

    /**
     * Calculate X based on anchor value with additional value
     */
    public int x(float anchor, int value)
    {
        return this.x + (int) ((this.w - value) * anchor);
    }

    /**
     * Calculate mid point X value
     */
    public int mx()
    {
        return this.x + (int) (this.w * 0.5F);
    }

    /**
     * Calculate mid point X value
     */
    public int mx(int value)
    {
        return this.x + (int) ((this.w - value) * 0.5F);
    }

    /**
     * Calculate end point X (right) value
     */
    public int ex()
    {
        return this.x + this.w;
    }

    /**
     * Calculate Y based on anchor value
     */
    public int y(float anchor)
    {
        return this.y + (int) (this.h * anchor);
    }

    /**
     * Calculate Y based on anchor value
     */
    public int y(float anchor, int value)
    {
        return this.y + (int) ((this.h - value) * anchor);
    }

    /**
     * Calculate mid point Y value
     */
    public int my()
    {
        return this.y + (int) (this.h * 0.5F);
    }

    /**
     * Calculate mid point Y value
     */
    public int my(int value)
    {
        return this.y + (int) ((this.h - value) * 0.5F);
    }

    /**
     * Calculate end point Y (bottom) value
     */
    public int ey()
    {
        return this.y + this.h;
    }

    /**
     * Draw a rect within the bound of this rect
     */
    public void draw(int color)
    {
        this.draw(color, 0, 0, 0, 0);
    }

    /**
     * Draw a rect within the bound of this rect
     */
    public void draw(int color, int offset)
    {
        this.draw(color, offset, offset, offset, offset);
    }

    /**
     * Draw a rect within the bound of this rect
     */
    public void draw(int color, int horizontal, int vertical)
    {
        this.draw(color, horizontal, vertical, horizontal, vertical);
    }

    /**
     * Draw a rect within the bound of this rect
     */
    public void draw(int color, int lx, int ty, int rx, int by)
    {
        Gui.drawRect(this.x + lx, this.y + ty, this.ex() - rx, this.ey() - by, color);
    }
}