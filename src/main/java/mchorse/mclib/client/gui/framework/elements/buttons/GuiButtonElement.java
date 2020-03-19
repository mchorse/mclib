package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.function.Consumer;

public class GuiButtonElement extends GuiClickElement<GuiButtonElement>
{
	public String label;

	public GuiButtonElement(Minecraft mc, String label, Consumer<GuiButtonElement> callback)
	{
		super(mc, callback);

		this.label = label;
	}

	@Override
	protected void drawSkin(GuiContext context)
	{
		int color = 0xff000000 + McLib.primaryColor.get();

		if (this.hover)
		{
			color = ColorUtils.multiplyColor(color, 0.85F);
		}

		if (McLib.enableBorders.get())
		{
			this.area.draw(0xff000000);
			Gui.drawRect(this.area.x + 1, this.area.y + 1, this.area.getX(1) - 1, this.area.getY(1) - 1, color);
		}
		else
		{
			this.area.draw(color);
		}

		int w = this.font.getStringWidth(this.label);

		this.font.drawStringWithShadow(this.label, this.area.getX(0.5F, -w), this.area.getY(0.5F) - this.font.FONT_HEIGHT / 2, this.hover ? 16777120 : 0xffffff);
	}
}