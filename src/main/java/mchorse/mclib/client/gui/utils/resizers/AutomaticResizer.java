package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AutomaticResizer extends BaseResizer
{
	public GuiElement parent;
	public int margin;
	public int padding;
	public int height;

	protected List<ChildResizer> resizers = new ArrayList<ChildResizer>();

	public AutomaticResizer(GuiElement parent, int margin)
	{
		this.parent = parent;
		this.margin = margin;

		this.setup();
	}

	/* Standard properties */

	public AutomaticResizer padding(int padding)
	{
		this.padding = padding;

		return this;
	}

	public AutomaticResizer height(int height)
	{
		this.height = height;

		return this;
	}

	public void reset()
	{
		this.resizers.clear();
	}

	/* Child management */

	public void setup()
	{
		for (IGuiElement child : this.parent.getChildren())
		{
			if (child instanceof GuiElement)
			{
				GuiElement element = (GuiElement) child;

				element.resizer(this.child(element));
			}
		}
	}

	public IResizer child(GuiElement element)
	{
		ChildResizer child = new ChildResizer(this, element);

		if (this.isCollecting())
		{
			this.resizers.add(child);
		}

		return child;
	}

	protected boolean isCollecting()
	{
		return true;
	}

	/* Miscellaneous */

	@Override
	public void add(GuiElement parent, GuiElement child)
	{
		child.resizer(this.child(child));
	}

	@Override
	public void remove(GuiElement parent, GuiElement child)
	{
		Iterator<ChildResizer> it = this.resizers.iterator();

		while (it.hasNext())
		{
			ChildResizer resizer = it.next();

			if (resizer.element == child)
			{
				it.remove();
				resizer.element.resizer(resizer.resizer);

				break;
			}
		}
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
	public int getW()
	{
		return 0;
	}

	@Override
	public int getH()
	{
		return 0;
	}
}