package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiDashboardPanels extends GuiPanelBase<GuiDashboardPanel>
{
	public GuiDashboardPanels(Minecraft mc)
	{
		super(mc, Direction.LEFT);
	}

	public void open()
	{
		for (GuiDashboardPanel panel : this.panels)
		{
			panel.open();
		}
	}

	public void close()
	{
		for (GuiDashboardPanel panel : this.panels)
		{
			panel.close();
		}
	}

	@Override
	public void setPanel(GuiDashboardPanel panel)
	{
		if (this.view.delegate != null)
		{
			this.view.delegate.disappear();
		}

		super.setPanel(panel);

		if (this.view.delegate != null)
		{
			this.view.delegate.appear();
		}
	}

	@Override
	protected void drawBackground(GuiContext context, int x, int y, int w, int h)
	{
		Gui.drawRect(x, y, x + w, y + h, 0xff111111);
	}
}