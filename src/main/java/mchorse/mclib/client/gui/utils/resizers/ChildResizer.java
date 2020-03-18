package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.utils.Area;

public class ChildResizer extends DecoratedResizer
{
	public DecoratedResizer parent;

	public ChildResizer(DecoratedResizer parent, IResizer resizer)
	{
		super(resizer);
		this.parent = parent;
	}

	@Override
	public void apply(Area area)
	{
		this.parent.apply(area, this.resizer);
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
