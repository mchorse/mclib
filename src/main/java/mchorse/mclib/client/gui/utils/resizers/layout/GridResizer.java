package mchorse.mclib.client.gui.utils.resizers.layout;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.resizers.AutomaticResizer;
import mchorse.mclib.client.gui.utils.resizers.ChildResizer;
import mchorse.mclib.client.gui.utils.resizers.IResizer;

public class GridResizer extends AutomaticResizer
{
	private int i;
	private int y;
	private int h;

	/**
	 * How many elements in a row
	 */
	private int items = 2;

	/**
	 * Whether this resizes changes the bounds of the area
	 */
	private boolean resizes = true;

	public static GridResizer apply(GuiElement element, int margin)
	{
		GridResizer resizer = new GridResizer(element, margin);

		element.flex().post(resizer);

		return resizer;
	}

	protected GridResizer(GuiElement parent, int margin)
	{
		super(parent, margin);
	}

	public GridResizer resizes(boolean resizes)
	{
		this.resizes = resizes;

		return this;
	}

	public GridResizer items(int items)
	{
		this.items = items;

		return this;
	}

	@Override
	public void apply(Area area)
	{
		this.i = this.y = this.h = 0;
	}

	@Override
	public void apply(Area area, IResizer resizer, ChildResizer child)
	{
		if (this.i != 0 && this.i % this.items == 0)
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
			h = this.height;
		}

		if (h <= 0)
		{
			h = w;
		}

		this.h = Math.max(this.h, h);

		area.set(x, y, w, h);

		this.i ++;
	}

	@Override
	public int getH()
	{
		if (this.resizes)
		{
			int i = 0;
			int y = 0;
			int max = 0;

			for (ChildResizer child : this.getResizers())
			{
				if (i != 0 && i % this.items == 0)
				{
					y += max + this.margin;
					max = 0;
					i = 0;
				}

				int w = (this.parent.area.w - this.padding * 2 - this.margin * (this.items - 1)) / this.items;
				int h = child.resizer == null ? 0 : child.resizer.getH();

				if (h <= 0)
				{
					h = this.height;
				}

				if (h <= 0)
				{
					h = w;
				}

				max = Math.max(max, h);

				i++;
			}

			return y + max + this.padding * 2;
		}

		return super.getH();
	}
}