package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;

public class ChildResizer extends DecoratedResizer
{
	public IParentResizer parent;
	public GuiElement element;
	private int x;
	private int y;
	private int w;
	private int h;

	public ChildResizer(IParentResizer parent, GuiElement element)
	{
		super(element.resizer());
		this.parent = parent;
		this.element = element;
	}

	@Override
	public void apply(Area area)
	{
		if (this.resizer != null)
		{
			this.resizer.apply(area);
		}

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
