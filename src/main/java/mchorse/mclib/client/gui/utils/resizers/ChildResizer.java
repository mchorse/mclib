package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.utils.Area;

public class ChildResizer implements IResizer
{
	public DecoratedResizer parent;
	public IResizer element;

	public ChildResizer(DecoratedResizer parent, IResizer element)
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
