package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;

public class ColumnResizer implements IResizer
{
	public Area parent;
	public int x;
	public int y;
	public int w;

	public int margin;
	public int padding;

	public ColumnResizer(Area parent, int margin, int padding)
	{
		this.parent = parent;
		this.margin = margin;
		this.padding = padding;
	}

	public void reset()
	{
		this.x = 0;
		this.y = 0;
		this.w = 0;
	}

	public IResizer child(GuiElement element)
	{
		return new ChildResizer(this, element.getResizer());
	}

	@Override
	public void apply(Area area)
	{
		throw new IllegalStateException("Can't be used with this resizer class!");
	}

	public void apply(Area area, IResizer resizer)
	{
		int w = resizer.getW();
		int h = resizer.getH();

		if (this.y + h > this.parent.h - this.padding * 2)
		{
			this.y = this.w = 0;
			this.x += this.w;
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

	public static class ChildResizer implements IResizer
	{
		public ColumnResizer parent;
		public IResizer element;

		public ChildResizer(ColumnResizer parent, IResizer element)
		{
			this.parent = parent;
			this.element = element;
		}

		@Override
		public void apply(Area area)
		{
			this.parent.apply(area, this.element);
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
		public int getW() {
			return 0;
		}

		@Override
		public int getH()
		{
			return 0;
		}
	}
}