package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;

import java.util.ArrayList;
import java.util.List;

public abstract class AutomaticResizer extends DecoratedResizer
{
	public GuiElement parent;
	public int margin;
	public int padding;

	protected List<ChildResizer> resizers = new ArrayList<ChildResizer>();
	protected boolean collectChildren = true;

	public AutomaticResizer(GuiElement parent, int margin)
	{
		super(parent.resizer());

		this.parent = parent;
		this.margin = margin;

		this.setup();
	}

	public AutomaticResizer dontCollect()
	{
		this.collectChildren = false;

		return this;
	}

	public AutomaticResizer padding(int padding)
	{
		this.padding = padding;

		return this;
	}

	public void reset()
	{
		this.resizers.clear();
	}

	public void setup()
	{
		for (IGuiElement child : this.parent.getChildren())
		{
			if (child instanceof GuiElement)
			{
				GuiElement element = (GuiElement) child;

				element.setResizer(this.child(element));
			}
		}
	}

	public void add(GuiElement... elements)
	{
		for (GuiElement element : elements)
		{
			element.setResizer(this.child(element));
		}
	}

	public IResizer child(GuiElement element)
	{
		ChildResizer child = new ChildResizer(this, element.getResizer());

		if (this.collectChildren)
		{
			this.resizers.add(child);
		}

		return child;
	}

	@Override
	public int getX()
	{
		return this.parent.area.x;
	}

	@Override
	public int getY()
	{
		return this.parent.area.y;
	}

	@Override
	public int getW()
	{
		return this.parent.area.w;
	}

	@Override
	public int getH()
	{
		return this.parent.area.h;
	}
}