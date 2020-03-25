package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

public class GuiToggleElement extends GuiClickElement<GuiToggleElement>
{
	public String label;
	public boolean state;

	public GuiToggleElement(Minecraft mc, String label, Consumer<GuiToggleElement> callback)
	{
		this(mc, label, false, callback);
	}

	public GuiToggleElement(Minecraft mc, String label, boolean state, Consumer<GuiToggleElement> callback)
	{
		super(mc, callback);

		this.label = label;
		this.state = state;
	}

	@Override
	protected void click()
	{
		this.state = !this.state;

		super.click();
	}

	@Override
	protected void drawSkin(GuiContext context)
	{
		this.font.drawStringWithShadow(this.label, this.area.x, this.area.getY(0.5F) - this.font.FONT_HEIGHT / 2, 0xffffff);

		/* Draw toggle switch */
		int w = 16;
		int h = 10;
		int x = this.area.getX(1) - w - 2;
		int y = this.area.getY(0.5F);
		int color = McLib.primaryColor.get();

		if (this.hover)
		{
			color = ColorUtils.multiplyColor(color, 0.85F);
		}

		/* Draw toggle background */
		Gui.drawRect(x, y - h / 2, x + w, y - h / 2 + h, 0xff000000);
		Gui.drawRect(x + 1, y - h / 2 + 1, x + w - 1, y - h / 2 + h - 1, 0xff000000 + (this.state ? color : (this.hover ? 0x3a3a3a : 0x444444)));

		if (this.state)
		{
			GuiDraw.drawHorizontalGradientRect(x + 1, y - h / 2 + 1, x + w / 2, y - h / 2 + h - 1, 0x66ffffff, 0x00ffffff);
		}
		else
		{
			GuiDraw.drawHorizontalGradientRect(x + w / 2, y - h / 2 + 1, x + w - 1, y - h / 2 + h - 1, 0x00000000, 0x66000000);
		}

		if (!this.isEnabled())
		{
			Gui.drawRect(x, y - h / 2, x + w, y - h / 2 + h, 0x88000000);
		}

		x += this.state ? w - 2 : 2;

		/* Draw toggle switch */
		Gui.drawRect(x - 4, y - 8, x + 4, y + 8, 0xff000000);
		Gui.drawRect(x - 3, y - 7, x + 3, y + 7, 0xffffffff);
		Gui.drawRect(x - 2, y - 6, x + 3, y + 7, 0xff888888);
		Gui.drawRect(x - 2, y - 6, x + 2, y + 6, 0xffbbbbbb);

		if (!this.isEnabled())
		{
			Gui.drawRect(x - 4, y - 8, x + 4, y + 8, 0x88000000);

			GuiDraw.drawOutlinedIcon(Icons.LOCKED, this.area.getX(1) - w / 2 - 2, y, 0xffffffff, 0.5F, 0.5F);
		}
	}
}