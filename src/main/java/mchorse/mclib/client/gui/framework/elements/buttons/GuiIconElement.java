package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

public class GuiIconElement extends GuiClickElement<GuiIconElement>
{
	public Icon icon;
	public Icon iconHover;
	public int hoverColor = 0xffe6e6e6;

	public GuiIconElement(Minecraft mc, Icon icon, Consumer<GuiIconElement> callback)
	{
		super(mc, callback);

		this.icon = icon;
		this.iconHover = icon;
	}

	public GuiIconElement setHovered(Icon icon)
	{
		this.iconHover = icon;

		return this;
	}

	@Override
	protected void drawSkin(GuiContext context)
	{
		Icon icon = this.hover ? this.iconHover : this.icon;

		if (this.isEnabled())
		{
			ColorUtils.bindColor(this.hover ? 0xffaaaaaa : 0xffffffff);
		}
		else
		{
			GlStateManager.color(0, 0, 0, 1F);
		}

		icon.render(this.area.getX(0.5F, icon.w), this.area.getY(0.5F, icon.h));
	}
}