package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

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
	public boolean label = true;

	public GuiColorElement(Minecraft mc, ValueInt value)
	{
		this(mc, value, null);
	}

	public GuiColorElement(Minecraft mc, ValueInt value, Consumer<Integer> callback)
	{
		this(mc, callback == null ? value::set : (integer) ->
		{
			value.set(integer);
			callback.accept(integer);
		});
		this.tooltip(IKey.lang(value.getTooltipKey()));

		if (value.getSubtype() == ValueInt.Subtype.COLOR_ALPHA)
		{
			this.picker.editAlpha();
		}

		this.picker.setColor(value.get());
	}

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
		this.picker.flex().anchor(0.5F, 0).wh(200, 85).bounds(GuiBase.getCurrent(), 2);

		this.flex().h(20);
	}

	public GuiColorElement noLabel()
	{
		this.label = false;

		return this;
	}

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (this.area.isInside(context))
		{
			if (!this.picker.hasParent())
			{
				this.getParentContainer().add(this.picker);
				this.picker.flex().xy(context.globalX(this.area.mx()), context.globalY(this.area.ey()) + 2);
				this.picker.resize();
			}
			else
			{
				this.picker.removeFromParent();
			}

			return true;
		}

		return false;
	}

	@Override
	public void draw(GuiContext context)
	{
		float factor = this.area.isInside(context) ? 0.85F : 1F;
		int padding = 0;

		if (McLib.enableBorders.get())
		{
			this.area.draw(0xff000000);
			this.picker.drawRect(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1);

			padding = 1;
		}
		else
		{
			this.picker.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey());
		}

		if (this.area.isInside(context))
		{
			this.area.draw(0x22000000, padding);
		}

		if (this.label)
		{
			String label = this.picker.color.stringify(this.picker.editAlpha);

			this.font.drawStringWithShadow(label, this.area.mx(this.font.getStringWidth(label)), this.area.my(this.font.FONT_HEIGHT - 1), 0xffffff);
		}

		GuiDraw.drawLockedArea(this);

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
		public boolean editAlpha;

		public Area red = new Area();
		public Area green = new Area();
		public Area blue = new Area();
		public Area alpha = new Area();

		public int dragging = -1;

		public GuiColorPickerElement(Minecraft mc, Consumer<Integer> callback)
		{
			super(mc);

			this.callback = callback;
			this.input = new GuiTextElement(mc, 7, (string) ->
			{
				this.setValue(ColorUtils.parseColor(string));
				this.callback();
			});

			this.input.flex().relative(this).set(5, 5, 0, 20).w(1, -35);

			this.hideTooltip().add(this.input);
		}

		public GuiColorPickerElement editAlpha()
		{
			this.editAlpha = true;
			this.input.field.setMaxStringLength(9);

			return this;
		}

		public void updateColor()
		{
			this.input.setText(this.color.stringify(this.editAlpha));
			this.callback();
		}

		protected void callback()
		{
			if (this.callback != null)
			{
				this.callback.accept(this.editAlpha ? this.color.getRGBAColor() : this.color.getRGBColor());
			}
		}

		public void setColor(int color)
		{
			this.setValue(color);
			this.input.setText(this.color.stringify(this.editAlpha));
		}

		public void setValue(int color)
		{
			this.color.set(color, this.editAlpha);
		}

		@Override
		public void resize()
		{
			super.resize();

			int c = this.editAlpha ? 4 : 3;
			int h = (this.area.h - 35) / c;
			int w = this.area.w - 10;
			int remainder = this.area.h - 35 - h * c;
			int y = this.area.y + 30;

			this.red.set(this.area.x + 5, y, w, h);

			if (this.editAlpha)
			{
				this.green.set(this.area.x + 5, y + h, w, h);
				this.blue.set(this.area.x + 5, y + h + h, w, h + remainder);
				this.alpha.set(this.area.x + 5, this.area.ey() - 5 - h, w, h);
			}
			else
			{
				this.green.set(this.area.x + 5, y + h, w, h + remainder);
				this.blue.set(this.area.x + 5, this.area.ey() - 5 - h, w, h);
			}
		}

		@Override
		public boolean mouseClicked(GuiContext context)
		{
			if (super.mouseClicked(context))
			{
				return true;
			}

			if (this.red.isInside(context))
			{
				this.dragging = 1;

				return true;
			}
			else if (this.green.isInside(context))
			{
				this.dragging = 2;

				return true;
			}
			else if (this.blue.isInside(context))
			{
				this.dragging = 3;

				return true;
			}
			else if (this.alpha.isInside(context) && this.editAlpha)
			{
				this.dragging = 4;

				return true;
			}

			if (!this.area.isInside(context))
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
		public void mouseReleased(GuiContext context)
		{
			super.mouseReleased(context);
			this.dragging = -1;
		}

		@Override
		public void draw(GuiContext context)
		{
			if (this.dragging >= 0)
			{
				float factor = (context.mouseX - (this.red.x + 7)) / (float) (this.red.w - 14);

				this.color.set(MathUtils.clamp(factor, 0, 1), this.dragging);
				this.updateColor();
			}

			int padding = GuiDraw.drawBorder(this.area, 0xffffffff);

			this.area.draw(0xffc6c6c6, padding + 1);
			this.drawRect(this.area.ex() - 25, this.area.y + 5, this.area.ex() - 5, this.area.y + 25);

			GuiDraw.drawOutline(this.area.ex() - 25, this.area.y + 5, this.area.ex() - 5, this.area.y + 25, 0x44000000);

			Color color = new Color();

			/* Draw red slider */
			color.copy(this.color).r = 0;
			int left = color.getRGBAColor();
			color.copy(this.color).r = 1;
			int right = color.getRGBAColor();

			GuiDraw.drawHorizontalGradientRect(this.red.x, this.red.y, this.red.ex(), this.red.ey(), left, right);

			/* Draw green slider */
			color.copy(this.color).g = 0;
			left = color.getRGBAColor();
			color.copy(this.color).g = 1;
			right = color.getRGBAColor();

			GuiDraw.drawHorizontalGradientRect(this.green.x, this.green.y, this.green.ex(), this.green.ey(), left, right);

			/* Draw blue slider */
			color.copy(this.color).b = 0;
			left = color.getRGBAColor();
			color.copy(this.color).b = 1;
			right = color.getRGBAColor();

			GuiDraw.drawHorizontalGradientRect(this.blue.x, this.blue.y, this.blue.ex(), this.blue.ey(), left, right);

			if (this.editAlpha)
			{
				/* Draw alpha slider */
				color.copy(this.color).a = 0;
				left = color.getRGBAColor();
				color.copy(this.color).a = 1;
				right = color.getRGBAColor();

				this.mc.renderEngine.bindTexture(Icons.ICONS);
				GuiUtils.drawContinuousTexturedBox(this.alpha.x, this.alpha.y, 0, 240, this.alpha.w, this.alpha.h, 16, 16, 0, 0);
				GuiDraw.drawHorizontalGradientRect(this.alpha.x, this.alpha.y, this.alpha.ex(), this.alpha.ey(), left, right);
			}

			GuiDraw.drawOutline(this.red.x, this.red.y, this.red.ex(), this.editAlpha ? this.alpha.ey() : this.blue.ey(), 0x44000000);

			this.drawMarker(this.red.x + 7 + (int) ((this.red.w - 14) * this.color.r), this.red.my());
			this.drawMarker(this.green.x + 7 + (int) ((this.green.w - 14) * this.color.g), this.green.my());
			this.drawMarker(this.blue.x + 7 + (int) ((this.blue.w - 14) * this.color.b), this.blue.my());

			if (this.editAlpha)
			{
				this.drawMarker(this.alpha.x + 7 + (int) ((this.alpha.w - 14) * this.color.a), this.alpha.my());
			}

			super.draw(context);
		}

		public void drawRect(int x1, int y1, int x2, int y2)
		{
			if (this.editAlpha)
			{
				this.mc.renderEngine.bindTexture(Icons.ICONS);
				GuiUtils.drawContinuousTexturedBox(x1, y1, 0, 240, x2 - x1, y2 - y1, 16, 16, 0, 0);
				this.drawAlphaPreviewQuad(x1, y1, x2, y2);
			}
			else
			{
				Gui.drawRect(x1, y1, x2, y2, this.color.getRGBAColor());
			}
		}

		private void drawAlphaPreviewQuad(int x1, int y1, int x2, int y2)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
			vertexbuffer.pos(x1, y1, 0).color(this.color.r, this.color.g, this.color.b, 1).endVertex();
			vertexbuffer.pos(x1, y2, 0).color(this.color.r, this.color.g, this.color.b, 1).endVertex();
			vertexbuffer.pos(x2, y1, 0).color(this.color.r, this.color.g, this.color.b, 1).endVertex();
			vertexbuffer.pos(x2, y1, 0).color(this.color.r, this.color.g, this.color.b, this.color.a).endVertex();
			vertexbuffer.pos(x1, y2, 0).color(this.color.r, this.color.g, this.color.b, this.color.a).endVertex();
			vertexbuffer.pos(x2, y2, 0).color(this.color.r, this.color.g, this.color.b, this.color.a).endVertex();
			tessellator.draw();

			GlStateManager.shadeModel(GL11.GL_FLAT);
			GlStateManager.disableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.enableTexture2D();
		}

		private void drawMarker(int x, int y)
		{
			Gui.drawRect(x - 4, y - 4, x + 4, y + 4, 0xff000000);
			Gui.drawRect(x - 3, y - 3, x + 3, y + 3, 0xffffffff);
			Gui.drawRect(x - 2, y - 2, x + 2, y + 2, 0xffc6c6c6);
		}
	}
}