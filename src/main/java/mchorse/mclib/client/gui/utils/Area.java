package mchorse.mclib.client.gui.utils;

import net.minecraft.client.gui.Gui;

/**
 * Utility class for boxes
 *
 * Used in GUI for rendering and locating cursor inside of the box purposes.
 */
public class Area
{
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

    /**
     * Check whether given position is inside of the rect
     */
    public boolean isInside(int x, int y)
    {
        return x >= this.x && x < this.x + this.w && y >= this.y && y < this.y + this.h;
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
    public int getX(float anchor)
    {
        return this.x + (int) (this.w * anchor);
    }

    /**
     * Calculate X based on anchor value with additional value
     */
    public int getX(float anchor, int value)
    {
        return this.x + (int) ((this.w - value) * anchor);
    }

    /**
     * Calculate Y based on anchor value
     */
    public int getY(float anchor)
    {
        return this.y + (int) (this.h * anchor);
    }

    /**
     * Calculate Y based on anchor value
     */
    public int getY(float anchor, int value)
    {
        return this.y + (int) ((this.h - value) * anchor);
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
        Gui.drawRect(this.x + lx, this.y + ty, this.getX(1) - rx, this.getY(1) - by, color);
    }
}