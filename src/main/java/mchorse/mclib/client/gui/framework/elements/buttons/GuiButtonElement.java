package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiButtonElement extends GuiClickElement<GuiButtonElement>
{
	public String label;

	public boolean custom;
	public int customColor;

	public GuiButtonElement(Minecraft mc, String label, Consumer<GuiButtonElement> callback)
	{
		super(mc, callback);

		this.label = label;
	}

	public GuiButtonElement color(int color)
	{
		this.custom = true;
		this.customColor = color & 0xffffff;

		return this;
	}

	@Override
	protected void drawSkin(GuiContext context)
	{
		int color = 0xff000000 + (this.custom ? this.customColor : McLib.primaryColor.get());

		if (this.hover)
		{
			color = ColorUtils.multiplyColor(color, 0.85F);
		}

		GuiDraw.drawBorder(this.area, color);

		int x = this.area.mx(this.font.getStringWidth(this.label));
		int y = this.area.my(this.font.FONT_HEIGHT - 1);

		this.font.drawStringWithShadow(this.label, x, y, this.hover ? 16777120 : 0xffffff);

		GuiDraw.drawLockedArea(this);
	}
}