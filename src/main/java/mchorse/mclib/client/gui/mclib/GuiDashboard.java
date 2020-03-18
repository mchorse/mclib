package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.events.RegisterDashboardPanels;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiDashboard extends GuiBase
{
	public GuiPanelBase<GuiElement> panels;

	public GuiDashboard(Minecraft mc)
	{
		this.panels = new GuiPanelBase<GuiElement>(mc, Direction.LEFT)
		{
			@Override
			protected void drawBackground(GuiContext context, int x, int y, int w, int h)
			{
				Gui.drawRect(x, y, x + w, y + h, 0xff111111);
			}
		};
		this.panels.resizer().parent(this.area).w(1, 0).h(1, 0);

		this.panels.registerPanel(new GuiConfig(mc), "Mods configuration", Icons.GEAR);
		McLib.EVENT_BUS.post(new RegisterDashboardPanels(this));

		this.elements.add(this.panels);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}