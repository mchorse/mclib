package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.client.gui.utils.Icon;

public class IconRenderer
{
    private Icon icon;
    private int w;
    private int h;
    /**
     * anchor where to place the nested icon
     */
    private float ax;
    private float ay;
    private int offsetX;
    private int offsetY;

    /**
     * @param icon
     * @param w the width of the icon or the icon wrapper
     * @param h the height of the icon or the icon wrapper
     * @param ax the anchor point of the icon to render inside the wrapper
     * @param ay the anchor point of the icon to render inside the wrapper
     */
    public IconRenderer(Icon icon, int w, int h, float ax, float ay, int offsetX, int offsetY)
    {
        this.icon = icon;
        this.w = w;
        this.h = h;
        this.ax = ax;
        this.ay = ay;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * @param icon
     * @param w the width of the icon or the icon wrapper
     * @param h the height of the icon or the icon wrapper
     * @param ax the anchor point of the icon to render inside the wrapper
     * @param ay the anchor point of the icon to render inside the wrapper
     */
    public IconRenderer(Icon icon, int w, int h, float ax, float ay)
    {
        this.icon = icon;
        this.w = w;
        this.h = h;
        this.ax = ax;
        this.ay = ay;
    }

    public IconRenderer(Icon icon, int w, int h)
    {
        this(icon, w, h, 0, 0);
    }

    public IconRenderer(Icon icon)
    {
        this(icon, icon.w, icon.h);
    }

    public Icon getIcon()
    {
        return this.icon;
    }

    public IconRenderer setIcon(Icon icon)
    {
        this.icon = icon;

        return this;
    }

    public int getW()
    {
        return this.w;
    }

    public IconRenderer setW(int w)
    {
        this.w = w;

        return this;
    }

    public int getH()
    {
        return this.h;
    }

    public int getOffsetX()
    {
        return this.offsetX;
    }

    public int getOffsetY()
    {
        return this.offsetY;
    }

    public IconRenderer setOffsetX(int offsetX)
    {
        this.offsetX = offsetX;

        return this;
    }

    public IconRenderer setOffsetY(int offsetY)
    {
        this.offsetY = offsetY;

        return this;
    }

    public IconRenderer setH(int h)
    {
        this.h = h;

        return this;
    }

    public float getAx()
    {
        return this.ax;
    }

    public IconRenderer setAnchor(float ax, float ay)
    {
        this.ax = ax;
        this.ay = ay;

        return this;
    }

    public float getAy()
    {
        return this.ay;
    }

    public void render(int x, int y)
    {
        this.render(x, y, 0, 0);
    }

    public void render(int x, int y, float ax, float ay)
    {
        x += this.w * this.ax + this.offsetX;
        y += this.h * this.ay + this.offsetY;

        this.icon.render(x, y, ax, ay);
    }
}
