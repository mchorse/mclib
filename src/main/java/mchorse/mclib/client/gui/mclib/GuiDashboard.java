package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.events.RegisterDashboardPanels;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

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

		this.panels.flex().relative(this.viewport).w(1, 0).h(1, 0);
		this.panels.registerPanel(config, I18n.format("mclib.gui.config.tooltip"), Icons.GEAR);
		this.panels.registerPanel(new GuiTest(mc), "Test", Icons.POSE);
		McLib.EVENT_BUS.post(new RegisterDashboardPanels(this));
		this.panels.setPanel(config);

		this.root.add(this.panels);
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
			this.element.flex().relative(this).xy(0.5F, 0.5F).anchor(0.5F, 0)
				.row(5).width(20).resize().padding(5);

			for (int i = 0, c = (int) (Math.random() * 5) + 5; i < c; i ++)
			{
				GuiIconElement element = new GuiIconElement(mc, Icons.GEAR, null);

				element.flex().wh(0, 20);

				this.element.add(element);
			}

			this.add(this.element);

			for (int i = 0; i < 5; i ++)
			{
				String category = i == 0 ? "" : "Category " + i;

				for (int j = 0; j < 5; j ++)
				{
					this.keys().register("Test", Keyboard.KEY_Q + i + j * (i + 1), () -> {}).category(category);
				}
			}
		}

		@Override
		public void draw(GuiContext context)
		{
			this.area.draw(0x88000000);
			this.element.area.draw(0x88000000);

			for (IGuiElement element : this.element.getChildren())
			{
				((GuiElement) element).area.draw(0x88000000);
			}

			super.draw(context);
		}
	}
}