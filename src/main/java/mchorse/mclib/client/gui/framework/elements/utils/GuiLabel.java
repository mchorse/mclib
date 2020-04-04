package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class GuiLabel extends GuiElement
{
	public String label;
	public int color;
	public float anchorX;
	public float anchorY;
	public int background;

	public static GuiLabel create(String label, int height)
	{
		GuiLabel element = new GuiLabel(Minecraft.getMinecraft(), label);

		element.flex().h(height);

		return element;
	}

	public static GuiLabel create(String label, int height, int color)
	{
		GuiLabel element = new GuiLabel(Minecraft.getMinecraft(), label, color);

		element.flex().h(height);

		return element;
	}

	public GuiLabel(Minecraft mc, String label)
	{
		this(mc, label, 0xffffff);
	}

	public GuiLabel(Minecraft mc, String label, int color)
	{
		super(mc);

		this.label = label;
		this.color = color;
	}

	public GuiLabel background(int color)
	{
		this.background = color;

		return this;
	}

	public GuiLabel anchor(float x, float y)
	{
		this.anchorX = x;
		this.anchorY = y;

		return this;
	}

	@Override
	public boolean isEnabled()
	{
		return false;
	}

	@Override
	public void setEnabled(boolean enabled)
	{}

	@Override
	public void draw(GuiContext context)
	{
		int a = this.background >> 24 & 0xff;

		int x = this.area.x(this.anchorX, this.font.getStringWidth(this.label));
		int y = this.area.y(this.anchorY, this.font.FONT_HEIGHT);

		if (a != 0)
		{
			Gui.drawRect(x - 3, y - 3, x + this.font.getStringWidth(this.label) + 3, y + this.font.FONT_HEIGHT + 3, this.background);
		}

		this.font.drawStringWithShadow(this.label, x, y, this.color);

		super.draw(context);
	}
}