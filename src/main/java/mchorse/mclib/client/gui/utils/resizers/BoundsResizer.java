package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.utils.MathUtils;

/**
 * Bounds resizer
 *
 * This resizer class allows to keep the element within the bounds of
 * the screen. Probably not a good idea to use it with scrolling elements...
 */
public class BoundsResizer extends DecoratedResizer
{
	public GuiContext context;
	public int padding;

	public BoundsResizer(IResizer resizer, GuiContext context, int padding)
	{
		super(resizer);

		this.context = context;
		this.padding = padding;
	}

	@Override
	public void apply(Area area)
	{
		this.resizer.apply(area);

		area.x = MathUtils.clamp(area.x, this.padding, this.context.screen.width - area.w - this.padding);
		area.y = MathUtils.clamp(area.y, this.padding, this.context.screen.height - area.h - this.padding);
	}

	@Override
	public int getX()
	{
		return MathUtils.clamp(this.resizer.getX(), this.padding, this.context.screen.width - this.resizer.getW() - this.padding);
	}

	@Override
	public int getY()
	{
		return MathUtils.clamp(this.resizer.getY(), this.padding, this.context.screen.height - this.resizer.getH() - this.padding);
	}

	@Override
	public int getW()
	{
		return this.resizer.getW();
	}

	@Override
	public int getH()
	{
		return this.resizer.getH();
	}
}