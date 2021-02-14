package mchorse.mclib.client.gui.utils.resizers.constraint;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiViewportStack;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.resizers.DecoratedResizer;
import mchorse.mclib.client.gui.utils.resizers.IResizer;
import mchorse.mclib.utils.MathUtils;

/**
 * Bounds resizer
 *
 * This resizer class allows to keep the element within the bounds of
 * current viewport
 */
public class BoundsResizer extends DecoratedResizer
{
    public GuiElement target;
    public int padding;

    private GuiViewportStack viewport = new GuiViewportStack();

    public static BoundsResizer apply(GuiElement element, GuiElement target, int padding)
    {
        BoundsResizer resizer = new BoundsResizer(element.resizer(), target, padding);

        element.flex().post(resizer);

        return resizer;
    }

    protected BoundsResizer(IResizer resizer, GuiElement target, int padding)
    {
        super(resizer);

        this.target = target;
        this.padding = padding;
    }

    @Override
    public void apply(Area area)
    {
        this.viewport.applyFromElement(this.target);

        Area viewport = this.viewport.getViewport();

        area.x = MathUtils.clamp(area.x, this.viewport.globalX(viewport.x) + this.padding, this.viewport.globalX(viewport.ex()) - area.w - this.padding);
        area.y = MathUtils.clamp(area.y, this.viewport.globalY(viewport.y) + this.padding, this.viewport.globalY(viewport.ey()) - area.h - this.padding);

        this.viewport.reset();
    }

    @Override
    public int getX()
    {
        return 0;
    }

    @Override
    public int getY()
    {
        return 0;
    }

    @Override
    public int getW()
    {
        return 0;
    }

    @Override
    public int getH()
    {
        return 0;
    }
}