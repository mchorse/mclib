package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;

public class RowResizer extends AutomaticResizer
{
	private int i;
	private int x;
	private int w;
	private int count;

	public RowResizer(GuiElement parent, int margin)
	{
		super(parent, margin);
	}

	@Override
	public void apply(Area area)
	{
		super.apply(area);

		this.i = this.x = this.w = 0;
		this.count = this.resizers.size();

		for (ChildResizer resizer : this.resizers)
		{
			int w = Math.max(resizer.resizer.getW(), 0);

			if (w > 0)
			{
				this.w += w;
				this.count --;
			}
		}
	}

	@Override
	public void apply(Area area, IResizer resizer)
	{
		int c = this.resizers.size();
		int original = this.parent.area.w - this.padding * 2 - this.margin * (c - 1);
		int w = this.count > 0 ? (original - this.w) / this.count : 0;
		int x = this.parent.area.x + this.padding + this.x;

		/* If resizer specifies its custom width, use that one instead */
		int cw = resizer == null ? 0 : resizer.getW();
		int ch = resizer == null ? 0 : resizer.getH();

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
}