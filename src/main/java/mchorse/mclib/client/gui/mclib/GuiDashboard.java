package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.resizers.layout.ColumnResizer;
import mchorse.mclib.client.gui.utils.resizers.layout.GridResizer;
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

		this.panels.flex().parent(this.viewport).w(1, 0).h(1, 0);
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
		private GuiElement frame;
		private GuiElement top;
		private GuiElement bottom;

		public GuiTest(Minecraft mc)
		{
			super(mc);

			this.frame = new GuiElement(mc);
			this.frame.flex().parent(this.area).w(180).h(1F);

			this.bottom = new GuiElement(mc);
			this.bottom.flex().relative(this.frame.resizer()).x(10).y(1, -10).w(1F, -20).anchor(0, 1);
			this.bottom.flex().post(new ColumnResizer(this.bottom, 5).vertical().stretch().height(20));

			GuiIconElement icon = new GuiIconElement(mc, Icons.REMOVE, null);
			GuiButtonElement close = new GuiButtonElement(mc, "Close", null);
			GuiButtonElement open = new GuiButtonElement(mc, "Open", null);

			icon.flex().wh(20, 20);
			close.flex().h(20);
			open.flex().h(20);

			GuiTextElement text = new GuiTextElement(mc, null);

			this.bottom.add(Elements.row(mc, 5, close, open, icon), text);

			this.top = new GuiScrollElement(mc);
			this.top.flex().relative(this.frame.resizer()).xy(10, 10).w(1F, -20).hTo(this.bottom.flex(), -5);
			this.top.flex().post(new ColumnResizer(this.top, 5).vertical().scroll().stretch().height(20));

			for (int i = 0; i < 20; i ++)
			{
				if (i % 5 == 0)
				{
					if (i % 10 == 0)
					{
						GuiElement element = new GuiElement(mc);

						element.flex().post(new GridResizer(element, 5).items(4));

						for (int j = 0; j < 16; j ++)
						{
							GuiButtonElement b = new GuiButtonElement(mc, "Grid " + j, null);

							b.flex().h(20);
							element.add(b);
						}

						this.top.add(element);
					}
					else
					{
						GuiButtonElement left = new GuiButtonElement(mc, "Left", null);
						GuiButtonElement right = new GuiButtonElement(mc, "Right", null);

						left.flex().h(20).w(40);
						right.flex().h(20);

						this.top.add(Elements.row(mc, 10, left, right));
					}
				}
				else
				{
					GuiColorElement button = new GuiColorElement(mc, null);
					button.picker.setColor((int) (Math.random() * 0xffffff));

					this.top.add(button);
				}
			}

			this.frame.add(this.bottom, this.top);
			this.add(this.frame);
		}

		@Override
		public void draw(GuiContext context)
		{
			super.draw(context);
		}
	}
}