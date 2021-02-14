package mchorse.mclib.client.gui.utils.resizers.layout;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.ScrollDirection;
import mchorse.mclib.client.gui.utils.resizers.AutomaticResizer;
import mchorse.mclib.client.gui.utils.resizers.ChildResizer;
import mchorse.mclib.client.gui.utils.resizers.IResizer;

public class ColumnResizer extends AutomaticResizer
{
    private int x;
    private int y;
    private int w;

    /**
     * Default width
     */
    private int width;

    /**
     * Keeps on adding elements vertically without shifting them into
     * the next row and resize the height of the element
     */
    private boolean vertical;

    /**
     * Stretch column to the full width of the parent element
     */
    private boolean stretch;

    /**
     * Scroll mode, this will automatically calculate the scroll area
     */
    private boolean scroll;

    /**
     * Place elements after it reached the bottom on the left, instead of the right
     */
    private boolean flip;

    public static ColumnResizer apply(GuiElement element, int margin)
    {
        ColumnResizer resizer = new ColumnResizer(element, margin);

        element.flex().post(resizer);

        return resizer;
    }

    protected ColumnResizer(GuiElement element, int margin)
    {
        super(element, margin);
    }

    public ColumnResizer width(int width)
    {
        this.width = width;

        return this;
    }

    public ColumnResizer vertical()
    {
        this.vertical = true;

        return this;
    }

    public ColumnResizer stretch()
    {
        this.stretch = true;

        return this;
    }

    public ColumnResizer scroll()
    {
        this.scroll = true;

        return this;
    }

    public ColumnResizer flip()
    {
        this.flip = true;

        return this;
    }

    @Override
    public void apply(Area area)
    {
        this.x = 0;
        this.y = 0;
        this.w = 0;
    }

    @Override
    public void apply(Area area, IResizer resizer, ChildResizer child)
    {
        int w = resizer == null ? this.width : resizer.getW();
        int h = resizer == null ? this.height : resizer.getH();

        if (w == 0)
        {
            w = this.width;
        }

        if (h == 0)
        {
            h = this.height;
        }

        if (this.stretch)
        {
            w = this.parent.area.w - this.padding * 2;
        }

        if (!this.vertical && this.y + h > this.parent.area.h - this.padding * 2)
        {
            this.x += (this.w + this.padding) * (this.flip ? -1 : 1);
            this.y = this.w = 0;
        }

        int x = this.parent.area.x + this.x + this.padding;
        int y = this.parent.area.y + this.y + this.padding;

        area.set(x, y, w, h);

        this.w = Math.max(this.w, w);
        this.y += h + this.margin;
    }

    @Override
    public void postApply(Area area)
    {
        if (this.scroll && this.parent.area instanceof ScrollArea)
        {
            ScrollArea scroll = (ScrollArea) this.parent.area;

            if (this.vertical && scroll.direction == ScrollDirection.VERTICAL)
            {
                scroll.scrollSize = this.y - this.margin + this.padding * 2;
            }
            else if (!this.vertical && scroll.direction == ScrollDirection.HORIZONTAL)
            {
                scroll.scrollSize = this.x + this.w + this.padding * 2;
            }

            scroll.clamp();
        }
    }

    @Override
    public int getH()
    {
        if (this.vertical && !this.scroll)
        {
            int y = this.padding * 2;

            for (ChildResizer child : this.getResizers())
            {
                int h = child.resizer == null ? 0 : child.resizer.getH();

                y += (h == 0 ? this.height : h) + this.margin;
            }

            return y - this.margin;
        }

        return super.getH();
    }
}