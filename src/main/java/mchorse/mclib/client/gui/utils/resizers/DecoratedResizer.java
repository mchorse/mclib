package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.utils.Area;

public abstract class DecoratedResizer implements IResizer
{
	public IResizer resizer;

	public DecoratedResizer(IResizer resizer)
	{
		this.resizer = resizer;
	}

	public void apply(Area area, IResizer resizer)
	{}
}