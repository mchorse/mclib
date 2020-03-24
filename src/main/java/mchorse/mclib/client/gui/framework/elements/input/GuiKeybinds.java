package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Keybind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

public class GuiKeybinds extends GuiScrollElement
{
	public List<Keybind> keybinds = new ArrayList<Keybind>();

	public GuiKeybinds(Minecraft mc)
	{
		super(mc);

		this.hideTooltip();
		this.scroll.opposite = true;
	}

	@Override
	public void draw(GuiContext context)
	{
		int cx = 40;
		Gui.drawRect(this.area.x, this.area.y, cx, this.area.getY(1F), 0xdd000000);
		GuiDraw.drawHorizontalGradientRect(cx, this.area.y, this.area.getX(1F), this.area.getY(1F), 0xdd000000, 0);

		super.draw(context);
	}

	@Override
	protected void preDraw(GuiContext context)
	{
		super.preDraw(context);

		int x = this.area.x + 10;
		int y = this.area.y + 20;
		int i = 0;

		for (Keybind keybind : this.keybinds)
		{
			String combo = keybind.getKeyCombo();
			int w = this.font.getStringWidth(combo);

			Gui.drawRect(x - 2, y + i - 2, x + w + 2, y + i + this.font.FONT_HEIGHT + 2, 0xff000000 + McLib.primaryColor.get());
			this.font.drawString(combo, x, y + i, 0xffffff);
			this.font.drawStringWithShadow(keybind.label, x + w + 6, y + i, 0xffffff);
			i += 17;
		}

		this.keybinds.clear();
		this.scroll.scrollSize = 40 + i - 17;
		this.scroll.clamp();
	}
}