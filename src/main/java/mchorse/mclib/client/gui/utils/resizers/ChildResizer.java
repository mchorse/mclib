package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;

public class ChildResizer extends DecoratedResizer
{
    public IParentResizer parent;
    public GuiElement element;
    private int x;
    private int y;
    private int w;
    private int h;

    public ChildResizer(IParentResizer parent, GuiElement element)
    {
        super(element.flex());
        this.parent = parent;
        this.element = element;
    }

    @Override
    public void apply(Area area)
    {
        if (this.resizer != null)
        {
            this.resizer.apply(area);
        }

        this.parent.apply(area, this.resizer, this);
        this.x = area.x;
        this.y = area.y;
        this.w = area.w;
        this.h = area.h;
    }

    @Override
    public void postApply(Area area)
    {
        if (this.resizer != null)
        {
            this.resizer.postApply(area);
        }
    }

    @Override
    public void add(GuiElement parent, GuiElement child)
    {
        if (this.resizer != null)
        {
            this.resizer.add(parent, child);
        }
    }

    @Override
    public void remove(GuiElement parent, GuiElement child)
    {
        if (this.resizer != null)
        {
            this.resizer.remove(parent, child);
        }
    }

    @Override
    public int getX()
    {
        return this.x;
    }

    @Override
    public int getY()
    {
        return this.y;
    }

    @Override
    public int getW()
    {
        return this.w;
    }

    @Override
    public int getH()
    {
        return this.h;
    }
}
