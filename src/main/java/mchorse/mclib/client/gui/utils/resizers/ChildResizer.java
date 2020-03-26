package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.utils.Area;

public class ChildResizer extends DecoratedResizer
{
	public DecoratedResizer parent;
	private int x;
	private int y;
	private int w;
	private int h;

	public ChildResizer(DecoratedResizer parent, IResizer resizer)
	{
		super(resizer);
		this.parent = parent;
	}

	@Override
	public void apply(Area area)
	{
		this.parent.apply(area, this.resizer);
		this.x = area.x;
		this.y = area.y;
		this.w = area.w;
		this.h = area.h;
	}

	@Override
	public int getX()
	{
		return this.x;
	}

	@Override
	public int getY()
	{
		return this.y;
	}

	@Override
	public int getW()
	{
		return this.w;
	}

	@Override
	public int getH()
	{
		return this.h;
	}
}
