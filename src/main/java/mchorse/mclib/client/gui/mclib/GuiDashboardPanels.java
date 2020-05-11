package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

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
	public GuiIconElement registerPanel(GuiDashboardPanel panel, IKey tooltip, Icon icon)
	{
		GuiIconElement element = super.registerPanel(panel, tooltip, icon);

		int key = this.getKeybind();

		if (key != -1)
		{
			element.keys()
				.register(IKey.comp(IKey.lang("mclib.gui.dashboard.open_panel"), tooltip), key, () -> element.clickItself(GuiBase.getCurrent()))
				.held(Keyboard.KEY_LMENU)
				.category(IKey.lang("mclib.gui.dashboard.category"));
		}

		return element;
	}

	protected int getKeybind()
	{
		int size = this.panels.size();

		switch (size)
		{
			case 1: return Keyboard.KEY_0;
			case 2: return Keyboard.KEY_1;
			case 3: return Keyboard.KEY_2;
			case 4: return Keyboard.KEY_3;
			case 5: return Keyboard.KEY_4;
			case 6: return Keyboard.KEY_5;
			case 7: return Keyboard.KEY_6;
			case 8: return Keyboard.KEY_7;
			case 9: return Keyboard.KEY_8;
			case 10: return Keyboard.KEY_9;
		}

		return -1;
	}

	@Override
	protected void drawBackground(GuiContext context, int x, int y, int w, int h)
	{
		Gui.drawRect(x, y, x + w, y + h, 0xff111111);
	}
}