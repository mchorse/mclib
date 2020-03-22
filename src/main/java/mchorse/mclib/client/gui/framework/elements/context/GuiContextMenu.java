package mchorse.mclib.client.gui.framework.elements.context;

import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;

public abstract class GuiContextMenu extends GuiElement
{
	private boolean close = false;

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
	public void mouseReleased(GuiContext context)
	{
		super.mouseReleased(context);

		if (!this.area.isInside(context.mouseX, context.mouseY) && this.close)
		{
			this.removeFromParent();
		}

		this.close = true;
	}

	@Override
	public void draw(GuiContext context)
	{
		this.area.draw(0xff000000);

		super.draw(context);
	}
}