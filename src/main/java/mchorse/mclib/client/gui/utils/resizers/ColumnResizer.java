package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;

public class ColumnResizer extends DecoratedResizer
{
	public Area parent;
	public int x;
	public int y;
	public int w;

	public int margin;
	public int padding;

	public ColumnResizer(GuiElement element, int margin, int padding)
	{
		super(element.resizer());

		this.parent = element.area;
		this.margin = margin;
		this.padding = padding;
	}

	public IResizer child(GuiElement element)
	{
		return new ChildResizer(this, element.getResizer());
	}

	@Override
	public void apply(Area area)
	{
		this.resizer.apply(area);

		this.x = 0;
		this.y = 0;
		this.w = 0;
	}

	@Override
	public void apply(Area area, IResizer resizer)
	{
		int w = resizer.getW();
		int h = resizer.getH();

		if (this.y + h > this.parent.h - this.padding * 2)
		{
			this.x += this.w + this.margin * 2;
			this.y = this.w = 0;
		}

		int x = this.parent.x + this.x + this.padding;
		int y = this.parent.y + this.y + this.padding;

		area.set(x, y, w, h);

		this.w = Math.max(this.w, w);
		this.y += h + this.margin;
	}

	@Override
	public int getX()
	{
		return this.parent.x;
	}

	@Override
	public int getY()
	{
		return this.parent.y;
	}

	@Override
	public int getW()
	{
		return this.x + this.w;
	}

	@Override
	public int getH()
	{
		return this.parent.h;
	}

}