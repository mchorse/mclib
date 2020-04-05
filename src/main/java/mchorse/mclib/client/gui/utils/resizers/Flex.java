package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;

/**
 * Flex class
 * 
 * This class is used to define resizing behavior for a 
 * {@link GuiElement}. 
 */
public class Flex implements IResizer
{
    public Unit x = new Unit();
    public Unit y = new Unit();
    public Unit w = new Unit();
    public Unit h = new Unit();

    public IResizer relative;
    public IResizer post;

    public void link(Flex flex)
    {
        this.x = flex.x;
        this.y = flex.y;
        this.w = flex.w;
        this.h = flex.h;

        this.relative = flex.relative;
        this.post = flex.post;
    }

    public Flex set(float x, float y, float w, float h)
    {
        return this.set(x, y, w, h, Measure.PIXELS);
    }

    public Flex set(float x, float y, float w, float h, Measure measure)
    {
        this.x.set(x, measure);
        this.y.set(y, measure);
        this.w.set(w, measure);
        this.h.set(h, measure);

        return this;
    }

    /* X */

    public Flex x(int value)
    {
        this.x.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Flex x(float value)
    {
        this.x.set(value, Measure.RELATIVE);

        return this;
    }

    public Flex x(float value, int offset)
    {
        this.x.set(value, Measure.RELATIVE, offset);

        return this;
    }

    /* Y */

    public Flex y(int value)
    {
        this.y.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Flex y(float value)
    {
        this.y.set(value, Measure.RELATIVE, 0);

        return this;
    }

    public Flex y(float value, int offset)
    {
        this.y.set(value, Measure.RELATIVE, offset);

        return this;
    }

    /* Width */

    public Flex w(int value)
    {
        this.w.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Flex w(float value)
    {
        this.w.set(value, Measure.RELATIVE, 0);

        return this;
    }

    public Flex w(float value, int offset)
    {
        this.w.set(value, Measure.RELATIVE, offset);

        return this;
    }

    public Flex wTo(IResizer flex)
    {
        this.w.target = flex;

        return this;
    }

    public Flex wTo(IResizer flex, int offset)
    {
        this.w.target = flex;
        this.w.offset = offset;

        return this;
    }

    public Flex wTo(IResizer flex, float anchor)
    {
        this.w.target = flex;
        this.w.targetAnchor = anchor;

        return this;
    }

    public Flex wTo(IResizer flex, float anchor, int offset)
    {
        this.w.target = flex;
        this.w.targetAnchor = anchor;
        this.w.offset = offset;

        return this;
    }

    /* Height */

    public Flex h(int value)
    {
        this.h.set(value, Measure.PIXELS, 0);

        return this;
    }

    public Flex h(float value)
    {
        this.h.set(value, Measure.RELATIVE, 0);

        return this;
    }

    public Flex h(float value, int offset)
    {
        this.h.set(value, Measure.RELATIVE, offset);

        return this;
    }

    public Flex hTo(IResizer flex)
    {
        this.h.target = flex;

        return this;
    }

    public Flex hTo(IResizer flex, int offset)
    {
        this.h.target = flex;
        this.h.offset = offset;

        return this;
    }

    public Flex hTo(IResizer flex, float anchor)
    {
        this.h.target = flex;
        this.h.targetAnchor = anchor;

        return this;
    }

    public Flex hTo(IResizer flex, float anchor, int offset)
    {
        this.h.target = flex;
        this.h.targetAnchor = anchor;
        this.h.offset = offset;

        return this;
    }

    /* Other variations */

    public Flex xy(int x, int y)
    {
        this.x.set(x, Measure.PIXELS);
        this.y.set(y, Measure.PIXELS);

        return this;
    }

    public Flex xy(float x, float y)
    {
        this.x.set(x, Measure.RELATIVE);
        this.y.set(y, Measure.RELATIVE);

        return this;
    }

    public Flex wh(int w, int h)
    {
        this.w.set(w, Measure.PIXELS);
        this.h.set(h, Measure.PIXELS);

        return this;
    }

    public Flex wh(float w, float h)
    {
        this.w.set(w, Measure.RELATIVE);
        this.h.set(h, Measure.RELATIVE);

        return this;
    }

    public Flex maxW(int max)
    {
        this.w.max = max;

        return this;
    }

    public Flex maxH(int max)
    {
        this.h.max = max;

        return this;
    }

    public Flex anchor(float x, float y)
    {
        this.x.anchor = x;
        this.y.anchor = y;

        return this;
    }

    public Flex anchorX(float x)
    {
        this.x.anchor = x;

        return this;
    }

    public Flex anchorY(float y)
    {
        this.y.anchor = y;

        return this;
    }

    /* Convenience methods */

    public Flex above(Flex flex, int offset)
    {
        return this.relative(flex).y(0, offset).anchorY(1);
    }

    public Flex under(Flex flex, int offset)
    {
        return this.relative(flex).y(1, offset);
    }

    public Flex left(Flex flex, int offset)
    {
        return this.relative(flex).x(0, offset).anchorX(1);
    }

    public Flex right(Flex flex, int offset)
    {
        return this.relative(flex).x(1, offset);
    }

    /* Hierarchy */

   public Flex relative(IResizer relative)
    {
        this.relative = relative;

        return this;
    }

    public Flex post(IResizer post)
    {
        this.post = post;

        return this;
    }

    @Override
    public void preApply(Area area)
    {}

    @Override
    public void apply(Area area)
    {
        if (this.post != null)
        {
            this.post.preApply(area);
        }

        area.w = this.getW();
        area.h = this.getH();
        area.x = this.getX();
        area.y = this.getY();

        if (this.post != null)
        {
            this.post.apply(area);
        }
    }

    @Override
    public void postApply(Area area)
    {
        if (this.post != null)
        {
            this.post.postApply(area);
        }
    }

    @Override
    public void add(GuiElement parent, GuiElement child)
    {
        if (this.post != null)
        {
            this.post.add(parent, child);
        }
    }

    @Override
    public void remove(GuiElement parent, GuiElement child)
    {
        if (this.post != null)
        {
            this.post.remove(parent, child);
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

        value += this.x.offset;

        if (this.x.anchor != 0)
        {
            value -= this.x.anchor * this.getW();
        }

        return value;
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

        value += this.y.offset;

        if (this.y.anchor != 0)
        {
            value -= this.y.anchor * this.getH();
        }

        return value;
    }

    public int getW()
    {
        if (this.w.target != null)
        {
            int w = this.w.targetAnchor == 0 ? 0 : (int) (this.w.target.getW() * this.w.targetAnchor);

            return (this.w.target.getX() + w) - this.getX() + this.w.offset;
        }

        int value = this.post == null ? 0 : this.post.getW();

        if (value != 0)
        {
            return value;
        }

        value = (int) this.w.value;

        if (this.relative != null && this.w.unit == Measure.RELATIVE)
        {
            value = (int) (this.relative.getW() * this.w.value);
        }

        value = value + this.w.offset;

        if (this.w.max > 0)
        {
            value = Math.min(value, this.w.max);
        }

        return value;
    }

    public int getH()
    {
        if (this.h.target != null)
        {
            int h = this.h.targetAnchor == 0 ? 0 : (int) (this.h.target.getH() * this.h.targetAnchor);

            return (this.h.target.getY() + h) - this.getY() + this.h.offset;
        }

        int value = this.post == null ? 0 : this.post.getH();

        if (value != 0)
        {
            return value;
        }

        value = (int) this.h.value;

        if (this.relative != null && this.h.unit == Measure.RELATIVE)
        {
            value = (int) (this.relative.getH() * this.h.value);
        }

        value = value + this.h.offset;

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
        public int offset;
        public int max;
        public float anchor;
        public Measure unit = Measure.PIXELS;
        public IResizer target;
        public float targetAnchor;

        public void set(float value, Measure unit)
        {
            this.set(value, unit, 0);
        }

        public void set(float value, Measure unit, int offset)
        {
            this.value = value;
            this.unit = unit;
            this.offset = offset;

            /* Reset target */
            this.target = null;
            this.targetAnchor = 0;
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