package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;

public class GridResizer extends AutomaticResizer
{
	public int items;

	private int i;
	private int y;
	private int h;
	private boolean doesResize = true;

	public GridResizer(GuiElement parent, int margin)
	{
		super(parent, margin);
	}

	public GridResizer items(int items)
	{
		this.items = items;

		return this;
	}

	public GridResizer dontResize()
	{
		this.doesResize = false;

		return this;
	}

	@Override
	public void apply(Area area)
	{
		super.apply(area);

		this.i = this.y = this.h = 0;
	}

	@Override
	public void apply(Area area, IResizer resizer)
	{
		if (this.i % this.items == 0)
		{
			this.y += this.h + this.margin;
			this.h = 0;
			this.i = 0;
		}

		int w = (this.parent.area.w - this.padding * 2 - this.margin * (this.items - 1)) / this.items;
		int h = resizer == null ? 0 : resizer.getH();
		int x = this.parent.area.x + this.padding + (w + this.margin) * this.i;
		int y = this.parent.area.y + this.padding + this.y;

		if (h <= 0)
		{
			h = w;
		}

		this.h = Math.max(this.h, h);

		area.set(x, y, w, h);

		if (this.doesResize)
		{
			this.parent.area.h = this.y + this.h + this.padding * 2;
		}

		this.i ++;
	}
}