package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.events.RegisterDashboardPanels;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class GuiDashboard extends GuiBase
{
	public GuiPanelBase<GuiElement> panels;

	public GuiDashboard(Minecraft mc)
	{
		GuiConfig config = new GuiConfig(mc);

		this.panels = new GuiPanelBase<GuiElement>(mc, Direction.LEFT)
		{
			@Override
			protected void drawBackground(GuiContext context, int x, int y, int w, int h)
			{
				Gui.drawRect(x, y, x + w, y + h, 0xff111111);
			}
		};

		this.panels.resizer().parent(this.area).w(1, 0).h(1, 0);
		this.panels.registerPanel(config, I18n.format("mclib.gui.config.tooltip"), Icons.GEAR);
		this.panels.registerPanel(new GuiTest(mc), "Test", Icons.POSE);
		McLib.EVENT_BUS.post(new RegisterDashboardPanels(this));
		this.panels.setPanel(config);

		this.elements.add(this.panels);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public static class GuiTest extends GuiElement
	{
		public GuiButtonElement button;
		public GuiCirculateElement circulate;

		public GuiTest(Minecraft mc)
		{
			super(mc);

			this.button = new GuiButtonElement(mc, "Test", (v) -> {});
			this.button.resizer().parent(this.area).set(10, 10, 80, 20);

			this.circulate = new GuiCirculateElement(mc, (v) -> {});
			this.circulate.addLabel("Test");
			this.circulate.addLabel("Not Test");
			this.circulate.addLabel("Very Test");
			this.circulate.resizer().parent(this.area).set(10, 40, 80, 20);

			GuiTrackpadElement trackpadElement = new GuiTrackpadElement(mc, (v) -> {});

			trackpadElement.resizer().parent(this.area).set(10, 70, 80, 20).x(0.5F, 0).y(0.5F, 0).anchor(0.5F, 0.5F);

			this.add(this.button);
			this.add(this.circulate);
			this.add(trackpadElement);
		}
	}
}