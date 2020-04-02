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
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
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

			this.frame = new GuiScrollElement(mc);
			this.frame.flex().parent(this.area).y(20).w(200).h(1, -20);
			ColumnResizer.apply(this.frame, 0).vertical().stretch().scroll().padding(20);

			for (int i = 0; i < 10; i ++)
			{
				GuiElement thing = new GuiElement(mc);

				GuiElement fields = new GuiElement(mc);
				ColumnResizer.apply(fields, 5).vertical().stretch().height(20).padding(5);

				GuiTrackpadElement yaw = new GuiTrackpadElement(mc, null);
				GuiTrackpadElement pitch = new GuiTrackpadElement(mc, null);
				GuiTrackpadElement roll = new GuiTrackpadElement(mc, null);
				GuiTrackpadElement fov = new GuiTrackpadElement(mc, null);

				yaw.flex().h(20);
				pitch.flex().h(20);
				roll.flex().h(20);
				fov.flex().h(20);

				fields.add(Elements.row(mc, 5, yaw, pitch), Elements.row(mc, 5, roll, fov));

				GuiElement header = new GuiElement(mc);
				header.flex().h(20);

				GuiElement enable = new GuiIconElement(mc, Icons.NONE, null);
				GuiElement remove = new GuiIconElement(mc, Icons.CLOSE, null);
				GuiElement moveUp = new GuiIconElement(mc, Icons.MOVE_UP, null);
				GuiElement moveDown = new GuiIconElement(mc, Icons.MOVE_DOWN, null);
				GuiElement copy = new GuiIconElement(mc, Icons.COPY, null);

				remove.flex().parent(header.area).set(0, 2, 16, 16).x(1, -18);
				enable.flex().relative(remove.resizer()).set(-20, 0, 16, 16);
				moveUp.flex().relative(enable.resizer()).set(-20, 0, 16, 8);
				moveDown.flex().relative(enable.resizer()).set(-20, 8, 16, 8);
				copy.flex().relative(moveUp.resizer()).set(-20, 0, 16, 16);

				ColumnResizer.apply(thing, 0).vertical().stretch();
				thing.add(header, fields);
				this.frame.add(thing);
			}

			this.add(this.frame);
		}

		@Override
		public void draw(GuiContext context)
		{
			this.frame.area.draw(0x88000000);

			super.draw(context);
		}
	}
}