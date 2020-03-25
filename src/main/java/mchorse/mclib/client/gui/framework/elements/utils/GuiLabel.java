package mchorse.mclib.client.gui.framework.elements.utils;

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
		int x = this.area.x(this.anchorX, this.font.getStringWidth(this.label));
		int y = this.area.y(this.anchorY, this.font.FONT_HEIGHT);

		this.font.drawStringWithShadow(this.label, x, y, this.color);

		super.draw(context);
	}
}