package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

public class GuiToggleElement extends GuiClickElement<GuiToggleElement>
{
	public IKey label;
	private boolean state;

	public GuiToggleElement(Minecraft mc, ValueBoolean value)
	{
		this(mc, value, null);
	}

	public GuiToggleElement(Minecraft mc, ValueBoolean value, Consumer<GuiToggleElement> callback)
	{
		this(mc, IKey.lang(value.getTitleKey()), value.get(), callback == null ? (toggle) -> value.set(toggle.isToggled()) : (toggle) ->
		{
			value.set(toggle.isToggled());
			callback.accept(toggle);
		});
		this.tooltip(IKey.lang(value.getTooltip()));
	}

	public GuiToggleElement(Minecraft mc, IKey label, Consumer<GuiToggleElement> callback)
	{
		this(mc, label, false, callback);
	}

	public GuiToggleElement(Minecraft mc, IKey label, boolean state, Consumer<GuiToggleElement> callback)
	{
		super(mc, callback);

		this.label = label;
		this.state = state;
	}

	public GuiToggleElement label(IKey label)
	{
		this.label = label;

		return this;
	}

	public GuiToggleElement toggled(boolean state)
	{
		this.state = state;

		return this;
	}

	public boolean isToggled()
	{
		return this.state;
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
		if (McLib.enableCheckboxRendering.get())
		{
			int y = this.area.my(this.font.FONT_HEIGHT - 1);

			Gui.drawRect(this.area.x, y - 3, this.area.x + 11, y + 8, 0xff000000 + McLib.primaryColor.get());

			if (McLib.enableBorders.get())
			{
				GuiDraw.drawOutline(this.area.x, y - 3, this.area.x + 11, y + 8, 0xff000000);
			}

			if (this.state)
			{
				this.font.drawStringWithShadow("x", this.area.x + 3, y - 2, 0xffffffff);
			}

			this.font.drawStringWithShadow(this.label.get(), this.area.x + 14, y, 0xffffff);

			if (!this.isEnabled())
			{
				Gui.drawRect(this.area.x, y - 3, this.area.x + 11, y + 8, 0x88000000);
				GuiDraw.drawOutlinedIcon(Icons.LOCKED, this.area.x + 5, y + 2, 0xffffffff, 0.5F, 0.5F);
			}
		}
		else
		{
			this.font.drawStringWithShadow(this.label.get(), this.area.x, this.area.my(this.font.FONT_HEIGHT - 1), 0xffffff);

			/* Draw toggle switch */
			int w = 16;
			int h = 10;
			int x = this.area.ex() - w - 2;
			int y = this.area.my();
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

				GuiDraw.drawOutlinedIcon(Icons.LOCKED, this.area.ex() - w / 2 - 2, y, 0xffffffff, 0.5F, 0.5F);
			}
		}
	}
}