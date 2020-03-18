package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;

public class GuiDashboard extends GuiBase
{
	public GuiPanelBase<GuiElement> panels;

	public GuiDashboard(Minecraft mc)
	{
		this.panels = new GuiPanelBase<GuiElement>(mc, Direction.LEFT);
		this.panels.registerPanel(new GuiConfig(mc), "Mods configuration", Icons.GEAR);

		this.panels.resizer().parent(this.area).w(1, 0).h(1, 0);

		this.elements.add(this.panels);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}