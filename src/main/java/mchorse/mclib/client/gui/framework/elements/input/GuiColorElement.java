package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

/**
 * Color GUI element
 *
 * This class is responsible for providing a way to edit colors, this element
 * itself is not editing the color, the picker element is the one that does color editing
 */
public class GuiColorElement extends GuiElement
{
	public GuiColorPickerElement picker;

	public GuiColorElement(Minecraft mc, Consumer<Integer> callback)
	{
		super(mc);

		this.picker = new GuiColorPickerElement(mc, (color) ->
		{
			if (callback != null)
			{
				callback.accept(color);
			}
		});

		this.picker.resizer().parent(this.area).x(0.5F, 0).y(1F, 2).anchor(0.5F, 0).wh(200, 80);
	}

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (!this.picker.hasParent() && this.area.isInside(context.mouseX, context.mouseY))
		{
			this.getParentContainer().add(this.picker);
			this.picker.resize();

			return true;
		}

		return false;
	}

	@Override
	public void draw(GuiContext context)
	{
		GuiDraw.drawBorder(this.area, this.picker.color.getRGBAColor());

		super.draw(context);
	}

	/**
	 * Color picker element
	 *
	 * This is the one that is responsible for picking colors
	 */
	public static class GuiColorPickerElement extends GuiElement
	{
		public Color color = new Color();
		public Consumer<Integer> callback;

		public GuiTextElement input;

		public Area red = new Area();
		public Area green = new Area();
		public Area blue = new Area();

		public GuiColorPickerElement(Minecraft mc, Consumer<Integer> callback)
		{
			super(mc);

			this.callback = callback;
			this.input = new GuiTextElement(mc, 7, (string) ->
			{
				this.setValue(ColorUtils.parseColor(string));
				this.callback();
			});

			this.input.resizer().parent(this.area).set(5, 5, 0, 20).w(1, -35);

			this.hideTooltip().add(this.input);
		}

		protected void callback()
		{
			if (this.callback != null)
			{
				this.callback.accept(this.color.getRGBColor());
			}
		}

		public void setColor(int color)
		{
			this.setValue(color);
			this.input.setText("#" + StringUtils.leftPad(Integer.toHexString(this.color.getRGBColor()), 6, '0'));
		}

		public void setValue(int color)
		{
			this.color.set(color, false);
		}

		@Override
		public void resize()
		{
			super.resize();

			int h = (this.area.h - 35) / 3;

			this.red.set(this.area.x + 5, this.area.y + 30, this.area.w - 10, h);
			this.green.set(this.area.x + 5, this.area.y + 30 + h, this.area.w - 10, h + (this.area.h - 35 - h * 3));
			this.blue.set(this.area.x + 5, this.area.getY(1F) - 5 - h, this.area.w - 10, h);
		}

		@Override
		public boolean mouseClicked(GuiContext context)
		{
			if (super.mouseClicked(context))
			{
				return true;
			}

			int x = context.mouseX;
			int y = context.mouseY;

			float factor = (x - this.red.x) / (float) (this.red.w - 1);

			if (this.red.isInside(x, y))
			{
				this.color.r = factor;
				this.setColor(this.color.getRGBColor());
				this.callback();

				return true;
			}
			else if (this.green.isInside(x, y))
			{
				this.color.g = factor;
				this.setColor(this.color.getRGBColor());
				this.callback();

				return true;
			}
			else if (this.blue.isInside(x, y))
			{
				this.color.b = factor;
				this.setColor(this.color.getRGBColor());
				this.callback();

				return true;
			}

			if (!this.area.isInside(x, y))
			{
				this.removeFromParent();

				return false;
			}
			else
			{
				return true;
			}
		}

		@Override
		public void draw(GuiContext context)
		{
			int padding = GuiDraw.drawBorder(this.area, 0xffffffff);

			this.area.draw(0xffc6c6c6, padding + 1);
			Gui.drawRect(this.area.getX(1) - 25, this.area.y + 5, this.area.getX(1) - 5, this.area.y + 25, this.color.getRGBAColor());
			GuiDraw.drawOutline(this.area.getX(1) - 25, this.area.y + 5, this.area.getX(1) - 5, this.area.y + 25, 0x44000000);

			Color color = new Color();

			/* Draw red slider */
			color.copy(this.color).r = 0;
			int left = color.getRGBAColor();
			color.copy(this.color).r = 1;
			int right = color.getRGBAColor();

			GuiDraw.drawHorizontalGradientRect(this.red.x, this.red.y, this.red.getX(1F), this.red.getY(1F), left, right, 0);

			/* Draw green slider */
			color.copy(this.color).g = 0;
			left = color.getRGBAColor();
			color.copy(this.color).g = 1;
			right = color.getRGBAColor();

			GuiDraw.drawHorizontalGradientRect(this.green.x, this.green.y, this.green.getX(1F), this.green.getY(1F), left, right, 0);

			/* Draw blue slider */
			color.copy(this.color).b = 0;
			left = color.getRGBAColor();
			color.copy(this.color).b = 1;
			right = color.getRGBAColor();

			GuiDraw.drawHorizontalGradientRect(this.blue.x, this.blue.y, this.blue.getX(1F), this.blue.getY(1F), left, right, 0);
			GuiDraw.drawOutline(this.red.x, this.red.y, this.red.getX(1F), this.blue.getY(1F), 0x44000000);

			this.drawMarker(this.red.x + 5 + (int) ((this.red.w - 10) * this.color.r), this.red.getY(0.5F));
			this.drawMarker(this.green.x + 5 + (int) ((this.green.w - 10) * this.color.g), this.green.getY(0.5F));
			this.drawMarker(this.blue.x + 5 + (int) ((this.blue.w - 10) * this.color.b), this.blue.getY(0.5F));

			super.draw(context);
		}

		private void drawMarker(int x, int y)
		{
			Gui.drawRect(x - 4, y - 4, x + 4, y + 4, 0xff000000);
			Gui.drawRect(x - 3, y - 3, x + 3, y + 3, 0xffffffff);
			Gui.drawRect(x - 2, y - 2, x + 2, y + 2, 0xffc6c6c6);
		}
	}
}