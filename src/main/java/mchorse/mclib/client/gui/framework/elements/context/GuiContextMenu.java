package mchorse.mclib.client.gui.framework.elements.context;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;

public abstract class GuiContextMenu extends GuiElement
{
	public GuiContextMenu(Minecraft mc)
	{
		super(mc);

		this.hideTooltip();
	}

	/**
	 * Set mouse coordiante
	 *
	 * In this method for subclasses, you should setup the resizer
	 */
	public abstract void setMouse(GuiContext context);

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (!this.area.isInside(context))
		{
			this.removeFromParent();
		}

		return false;
	}

	@Override
	public void draw(GuiContext context)
	{
		this.area.draw(0xff000000);

		super.draw(context);
	}
}