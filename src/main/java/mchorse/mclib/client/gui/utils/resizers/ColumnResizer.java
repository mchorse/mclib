package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.ScrollArea;

public class ColumnResizer extends AutomaticResizer
{
	private int x;
	private int y;
	private int w;
	private boolean vertical;
	private boolean stretch;

	public ColumnResizer(GuiElement element, int margin)
	{
		super(element, margin);

		this.dontCollect();
		this.resizers.clear();
	}

	/**
	 * Instead of moving elements onto the next column,
	 * keep going on and stretch the elements to the full width
	 * and recalculate
	 */
	public ColumnResizer stretch()
	{
		this.stretch = true;

		return this;
	}

	/**
	 * Instead of moving elements onto the next column,
	 * keep going on and stretch the elements to the full width and
	 * also if given area is scrollable automatically calculate its
	 * scrolling size
	 */
	public ColumnResizer vertical()
	{
		this.vertical = true;

		return this;
	}

	@Override
	public void apply(Area area)
	{
		super.apply(area);

		this.x = 0;
		this.y = 0;
		this.w = 0;
	}

	@Override
	public void apply(Area area, IResizer resizer)
	{
		int w = resizer == null ? 0 : resizer.getW();
		int h = resizer == null ? 0 : resizer.getH();

		if (this.stretch || this.vertical)
		{
			w = this.parent.area.w - this.padding * 2;
		}
		else if (this.y + h > this.parent.area.h - this.padding * 2)
		{
			this.x += this.w + this.margin * 2;
			this.y = this.w = 0;
		}

		int x = this.parent.area.x + this.x + this.padding;
		int y = this.parent.area.y + this.y + this.padding;

		area.set(x, y, w, h);

		this.w = Math.max(this.w, w);
		this.y += h + this.margin;

		if (this.vertical && this.parent.area instanceof ScrollArea)
		{
			((ScrollArea) this.parent.area).scrollSize = this.y - this.margin + this.padding * 2;
		}
		else if (this.stretch)
		{
			this.parent.area.h = this.y - this.margin + this.padding * 2;
		}
	}

	public int getSize()
	{
		if (this.stretch)
		{
			return this.y - this.margin;
		}

		return this.x + this.w + this.padding * 2;
	}
}