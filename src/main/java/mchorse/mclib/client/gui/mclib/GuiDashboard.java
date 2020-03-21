package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.resizers.GridResizer;
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
		public GuiElement element;

		public GuiTest(Minecraft mc)
		{
			super(mc);

			this.element = new GuiElement(mc);

			for (int i = 0; i < 20; i ++)
			{
				GuiElement element;

				if (Math.random() > 0.5)
				{
					element = new GuiButtonElement(mc, "Test " + i, (b) -> {});
				}
				else
				{
					element = new GuiTrackpadElement(mc, (v) -> {});
				}

				element.resizer().set(0, 0, 0, 20);
				this.element.add(element);
			}

			this.element.resizer().parent(this.area).set(0, 0, 400, 400);
			this.element.setResizer(new GridResizer(this.element, 0).items(4).padding(10));

			this.add(this.element);
		}

		@Override
		public void draw(GuiContext context)
		{
			this.element.area.draw(0x88000000);

			super.draw(context);
		}
	}
}