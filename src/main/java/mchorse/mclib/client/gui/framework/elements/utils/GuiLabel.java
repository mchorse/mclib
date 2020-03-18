package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;

public class GuiLabel extends GuiElement
{
	public String label;
	public int color;
	public float anchorX;
	public float anchorY;

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
		int w = this.font.getStringWidth(this.label);
		int h = this.font.FONT_HEIGHT;

		int x = this.area.getX(this.anchorX) - (int) (w * this.anchorX);
		int y = this.area.getY(this.anchorY) - (int) (h * this.anchorY);

		this.font.drawStringWithShadow(this.label, x, y, this.color);

		super.draw(context);
	}
}