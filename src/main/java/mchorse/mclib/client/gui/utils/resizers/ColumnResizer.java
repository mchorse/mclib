package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;

public class ColumnResizer extends AutomaticResizer
{
	private int x;
	private int y;
	private int w;

	public ColumnResizer(GuiElement element, int margin)
	{
		super(element, margin);
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

		if (this.y + h > this.parent.area.h - this.padding * 2)
		{
			this.x += this.w + this.margin * 2;
			this.y = this.w = 0;
		}

		int x = this.parent.area.x + this.x + this.padding;
		int y = this.parent.area.y + this.y + this.padding;

		area.set(x, y, w, h);

		this.w = Math.max(this.w, w);
		this.y += h + this.margin;
	}

	public int getSize()
	{
		return this.x + this.w + this.padding * 2;
	}
}