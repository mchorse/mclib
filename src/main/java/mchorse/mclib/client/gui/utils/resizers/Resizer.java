package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;

/**
 * Resizer class
 * 
 * This class is used to define resizing behavior for a 
 * {@link GuiElement}. 
 */
public class Resizer implements IResizer
{
    public Unit x = new Unit();
    public Unit y = new Unit();
    public Unit w = new Unit();
    public Unit h = new Unit();

    public IResizer relative;
    public Area parent;

    public Resizer set(float x, float y, float w, float h)
    {
        return this.set(x, y, w, h, Measure.PIXELS);
    }

    public Resizer set(float x, float y, float w, float h, Measure measure)
    {
        this.x.set(x, measure);
        this.y.set(y, measure);
        this.w.set(w, measure);
        this.h.set(h, measure);

        return this;
    }

    public Resizer x(int value)
    {
        this.x.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Resizer x(float value, int padding)
    {
        this.x.set(value, Measure.RELATIVE, padding);

        return this;
    }

    public Resizer y(int value)
    {
        this.y.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Resizer y(float value, int padding)
    {
        this.y.set(value, Measure.RELATIVE, padding);

        return this;
    }

    public Resizer w(int value)
    {
        this.w.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Resizer w(float value, int padding)
    {
        this.w.set(value, Measure.RELATIVE, padding);

        return this;
    }

    public Resizer h(int value)
    {
        this.h.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Resizer h(float value, int padding)
    {
        this.h.set(value, Measure.RELATIVE, padding);

        return this;
    }

    public Resizer xy(int x, int y)
    {
        this.x.set(x, Measure.PIXELS);
        this.y.set(y, Measure.PIXELS);

        return this;
    }

    public Resizer xy(float x, float y)
    {
        this.x.set(x, Measure.RELATIVE);
        this.y.set(y, Measure.RELATIVE);

        return this;
    }

    public Resizer wh(int w, int h)
    {
        this.w.set(w, Measure.PIXELS);
        this.h.set(h, Measure.PIXELS);

        return this;
    }

    public Resizer wh(float w, float h)
    {
        this.w.set(w, Measure.RELATIVE);
        this.h.set(h, Measure.RELATIVE);

        return this;
    }

    public Resizer maxW(int max)
    {
        this.w.max = max;

        return this;
    }

    public Resizer maxH(int max)
    {
        this.h.max = max;

        return this;
    }

    public Resizer anchor(float x, float y)
    {
        this.x.anchor = x;
        this.y.anchor = y;

        return this;
    }

    public Resizer relative(IResizer relative)
    {
        this.relative = relative;
        this.parent = null;

        return this;
    }

    public Resizer parent(Area parent)
    {
        this.parent = parent;
        this.relative = null;

        return this;
    }

    @Override
    public void apply(Area area)
    {
        if (this.w.enabled) area.w = this.getW();
        if (this.h.enabled) area.h = this.getH();

        if (this.x.enabled)
        {
            area.x = this.getX();

            if (this.w.enabled)
            {
                area.x -= area.w * this.x.anchor;
            }
        }

        if (this.y.enabled)
        {
            area.y = this.getY();

            if (this.h.enabled)
            {
                area.y -= area.h * this.y.anchor;
            }
        }
    }

    public int getX()
    {
        int value = (int) this.x.value;

        if (this.relative != null)
        {
            value += this.relative.getX();

            if (this.x.unit == Measure.RELATIVE)
            {
                value = this.relative.getX() + (int) (this.relative.getW() * this.x.value);
            }
        }
        else if (this.parent != null)
        {
            value += this.parent.x;

            if (this.x.unit == Measure.RELATIVE)
            {
                value = this.parent.x + (int) (this.parent.w * this.x.value);
            }
        }

        return value + this.x.padding;
    }

    public int getY()
    {
        int value = (int) this.y.value;

        if (this.relative != null)
        {
            value += this.relative.getY();

            if (this.y.unit == Measure.RELATIVE)
            {
                value = this.relative.getY() + (int) (this.relative.getH() * this.y.value);
            }
        }
        else if (this.parent != null)
        {
            value += this.parent.y;

            if (this.y.unit == Measure.RELATIVE)
            {
                value = this.parent.y + (int) (this.parent.h * this.y.value);
            }
        }

        return value + this.y.padding;
    }

    public int getW()
    {
        int value = (int) this.w.value;

        if (this.relative != null && this.w.unit == Measure.RELATIVE)
        {
            value = (int) (this.relative.getW() * this.w.value);
        }
        else if (this.parent != null && this.w.unit == Measure.RELATIVE)
        {
            value = (int) (this.parent.w * this.w.value);
        }

        value = value + this.w.padding;

        if (this.w.max > 0)
        {
            value = Math.min(value, this.w.max);
        }

        return value;
    }

    public int getH()
    {
        int value = (int) this.h.value;

        if (this.relative != null && this.h.unit == Measure.RELATIVE)
        {
            value = (int) (this.relative.getH() * this.h.value);
        }
        else if (this.parent != null && this.h.unit == Measure.RELATIVE)
        {
            value = (int) (this.parent.h * this.h.value);
        }

        value = value + this.h.padding;

        if (this.h.max > 0)
        {
            value = Math.min(value, this.h.max);
        }

        return value;
    }

    /**
     * Unit class
     */
    public static class Unit
    {
        public float value;
        public int padding;
        public int max;
        public float anchor;
        public boolean enabled = true;
        public Measure unit = Measure.PIXELS;

        public void set(float value, Measure unit)
        {
            this.set(value, unit, 0);
        }

        public void set(float value, Measure unit, int padding)
        {
            this.value = value;
            this.unit = unit;
            this.padding = padding;
        }

        public void disable()
        {
            this.enabled = false;
        }
    }

    /**
     * Unit measurement for sizer class. This determines logic for 
     * calculating units.
     * 
     * {@link Measure#PIXELS} are absolute. Meanwhile 
     * {@link Measure#RELATIVE} are percentage (or rather a scalar 
     * between 0 and 1 equaling to 0% to 100%). 
     */
    public static enum Measure
    {
        PIXELS, RELATIVE;
    }
}