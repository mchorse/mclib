package mchorse.mclib.client.gui.utils.resizers.layout;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.resizers.AutomaticResizer;
import mchorse.mclib.client.gui.utils.resizers.ChildResizer;
import mchorse.mclib.client.gui.utils.resizers.IResizer;

public class RowResizer extends AutomaticResizer
{
	private int i;
	private int x;
	private int w;
	private int count;

	public static RowResizer apply(GuiElement element, int margin)
	{
		RowResizer resizer = new RowResizer(element, margin);

		element.flex().post(resizer);

		return resizer;
	}

	protected RowResizer(GuiElement parent, int margin)
	{
		super(parent, margin);
	}

	@Override
	public void apply(Area area)
	{
		this.i = this.x = this.w = 0;
		this.count = this.resizers.size();

		for (ChildResizer resizer : this.resizers)
		{
			int w = Math.max(resizer.resizer == null ? 0 : resizer.resizer.getW(), 0);

			if (w > 0)
			{
				this.w += w;
				this.count --;
			}
		}
	}

	@Override
	public void apply(Area area, IResizer resizer, ChildResizer child)
	{
		int c = this.resizers.size();
		int original = this.parent.area.w - this.padding * 2 - this.margin * (c - 1);
		int w = this.count > 0 ? (original - this.w) / this.count : 0;
		int x = this.parent.area.x + this.padding + this.x;

		/* If resizer specifies its custom width, use that one instead */
		int cw = resizer == null ? 0 : resizer.getW();
		int ch = resizer == null ? this.height : resizer.getH();

		cw = cw > 0 ? cw : w;

		/* Readjust the middle element width to balance out int imprecision */
		if (this.i == c / 2)
		{
			int diff = original - this.w - w * this.count;

			if (diff > 0)
			{
				cw += diff;
			}
		}

		area.set(x, this.parent.area.y + this.padding, cw, ch > 0 ? ch : this.parent.area.h - this.padding * 2);

		this.x += cw + this.margin;
		this.i ++;
	}

	@Override
	public int getH()
	{
		int h = 0;

		for (ChildResizer child : this.resizers)
		{
			h = child.resizer == null ? 0 : child.resizer.getH();
		}

		if (h == 0)
		{
			h = this.height;
		}

		return h + this.padding * 2;
	}
}