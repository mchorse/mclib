package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

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

		GuiDraw.drawBorder(this.area, color);

		int w = this.font.getStringWidth(this.label);

		this.font.drawStringWithShadow(this.label, this.area.getX(0.5F, w), this.area.getY(0.5F) - this.font.FONT_HEIGHT / 2, this.hover ? 16777120 : 0xffffff);

		GuiDraw.drawLockedArea(this);
	}
}