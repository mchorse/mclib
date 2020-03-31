package mchorse.mclib.client.gui.utils.resizers.constraint;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.resizers.DecoratedResizer;
import mchorse.mclib.client.gui.utils.resizers.IResizer;
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
		area.x = MathUtils.clamp(area.x, this.padding, this.context.screen.width - area.w - this.padding);
		area.y = MathUtils.clamp(area.y, this.padding, this.context.screen.height - area.h - this.padding);
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