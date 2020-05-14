package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.values.ValueInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;

public class GuiKeybindElement extends GuiElement
{
	public int keybind;
	public boolean enabled;
	public Consumer<Integer> callback;

	public GuiKeybindElement(Minecraft mc, ValueInt value)
	{
		this(mc, value, null);
	}

	public GuiKeybindElement(Minecraft mc, ValueInt value, Consumer<Integer> callback)
	{
		this(mc, callback == null ? value::set : (integer) ->
		{
			value.set(integer);
			callback.accept(integer);
		});
		this.setKeybind(value.get());
		this.tooltip(IKey.lang(value.getTooltipKey()));
	}

	public GuiKeybindElement(Minecraft mc, Consumer<Integer> callback)
	{
		super(mc);

		this.callback = callback;
		this.flex().h(20);
	}

	public void setKeybind(int keybind)
	{
		this.keybind = keybind;
	}

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (this.area.isInside(context) && context.mouseButton == 0)
		{
			this.enabled = true;
		}

		return this.area.isInside(context);
	}

	@Override
	public boolean keyTyped(GuiContext context)
	{
		if (super.keyTyped(context))
		{
			return true;
		}

		if (this.enabled)
		{
			this.keybind = context.keyCode;
			this.enabled = false;

			if (this.callback != null)
			{
				this.callback.accept(context.keyCode);
			}

			return true;
		}

		return false;
	}

	@Override
	public void draw(GuiContext context)
	{
		if (this.enabled)
		{
			GuiDraw.drawBorder(this.area, 0xff000000 + McLib.primaryColor.get());

			int x = this.area.mx();
			int y = this.area.my();
			int a = (int) (Math.sin((context.tick + context.partialTicks) / 2D) * 127.5 + 127.5) << 24;

			Gui.drawRect(x - 1, y - 6, x + 1, y + 6, a + 0xffffff);
		}
		else
		{
			this.area.draw(0xff000000);

			/* Avoid a crash due to index out of bounds */
			int keybind = this.keybind < 0 || this.keybind >= Keyboard.KEYBOARD_SIZE ? 0 : this.keybind;

			this.drawCenteredString(this.font, Keyboard.getKeyName(keybind), this.area.mx(), this.area.my() - this.font.FONT_HEIGHT / 2, 0xffffff);
		}

		super.draw(context);
	}
}