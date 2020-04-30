package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Stack;

public class GuiDraw
{
	private final static Stack<Area> scissors = new Stack<Area>();

	public static void scissor(int x, int y, int w, int h, GuiContext context)
	{
	    scissor(x - context.shiftX, y - context.shiftY, w, h, context.screen.width, context.screen.height);
	}

	/**
	 * Scissor (clip) the screen
	 */
	public static void scissor(int x, int y, int w, int h, int sw, int sh)
	{
	    Area scissor = scissors.isEmpty() ? null : scissors.peek();

	    /* If it was scissored before, then clamp to the bounds of the last one */
		if (scissor != null)
		{
			w += Math.min(x - scissor.x, 0);
			h += Math.min(y - scissor.y, 0);
			x = MathUtils.clamp(x, scissor.x, scissor.ex());
			y = MathUtils.clamp(y, scissor.y, scissor.ey());
			w = MathUtils.clamp(w, 0, scissor.ex() - x);
			h = MathUtils.clamp(h, 0, scissor.ey() - y);
		}

		scissor = new Area(x, y, w, h);
	    scissorArea(x, y, w, h, sw, sh);
		scissors.add(scissor);
	}

	private static void scissorArea(int x, int y, int w, int h, int sw, int sh)
	{
		/* Clipping area around scroll area */
		Minecraft mc = Minecraft.getMinecraft();

		float rx = (float) Math.ceil(mc.displayWidth / (double) sw);
		float ry = (float) Math.ceil(mc.displayHeight / (double) sh);

		int xx = (int) (x * rx);
		int yy = (int) (mc.displayHeight - (y + h) * ry);
		int ww = (int) (w * rx);
		int hh = (int) (h * ry);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		if (ww == 0 || hh == 0)
		{
			GL11.glScissor(0, 0, 1, 1);
		}
		else
		{
			GL11.glScissor(xx, yy, ww, hh);
		}
	}

	public static void unscissor(GuiContext context)
	{
		unscissor(context.screen.width, context.screen.height);
	}

	public static void unscissor(int sw, int sh)
	{
		scissors.pop();

		if (scissors.isEmpty())
		{
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
		else
		{
			Area area = scissors.peek();

			scissorArea(area.x, area.y, area.w, area.h, sw, sh);
		}
	}

	public static void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
	{
		drawHorizontalGradientRect(left, top, right, bottom, startColor, endColor, 0);
	}

	/**
	 * Draws a rectangle with a horizontal gradient between with specified
	 * colors, the code is borrowed form drawGradient()
	 */
	public static void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, float zLevel)
	{
	    float a1 = (startColor >> 24 & 255) / 255.0F;
	    float r1 = (startColor >> 16 & 255) / 255.0F;
	    float g1 = (startColor >> 8 & 255) / 255.0F;
	    float b1 = (startColor & 255) / 255.0F;
	    float a2 = (endColor >> 24 & 255) / 255.0F;
	    float r2 = (endColor >> 16 & 255) / 255.0F;
	    float g2 = (endColor >> 8 & 255) / 255.0F;
	    float b2 = (endColor & 255) / 255.0F;

	    GlStateManager.disableTexture2D();
	    GlStateManager.enableBlend();
	    GlStateManager.disableAlpha();
	    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	    GlStateManager.shadeModel(GL11.GL_SMOOTH);

	    Tessellator tessellator = Tessellator.getInstance();
	    BufferBuilder buffer = tessellator.getBuffer();

	    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
	    buffer.pos(right, top, zLevel).color(r2, g2, b2, a2).endVertex();
	    buffer.pos(left, top, zLevel).color(r1, g1, b1, a1).endVertex();
	    buffer.pos(left, bottom, zLevel).color(r1, g1, b1, a1).endVertex();
	    buffer.pos(right, bottom, zLevel).color(r2, g2, b2, a2).endVertex();

	    tessellator.draw();

	    GlStateManager.shadeModel(GL11.GL_FLAT);
	    GlStateManager.disableBlend();
	    GlStateManager.enableAlpha();
	    GlStateManager.enableTexture2D();
	}

	public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH)
	{
	    drawBillboard(x, y, u, v, w, h, textureW, textureH, 0);
	}

	/**
	 * Draw a textured quad with given UV, dimensions and custom texture size
	 */
	public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH, float z)
	{
	    float tw = 1F / textureW;
	    float th = 1F / textureH;

	    Tessellator tessellator = Tessellator.getInstance();
	    BufferBuilder buffer = tessellator.getBuffer();

	    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	    buffer.pos(x, y + h, z).tex(u * tw, (v + h) * th).endVertex();
	    buffer.pos(x + w, y + h, z).tex((u + w) * tw, (v + h) * th).endVertex();
	    buffer.pos(x + w, y, z).tex((u + w) * tw, v * th).endVertex();
	    buffer.pos(x, y, z).tex(u * tw, v * th).endVertex();

	    tessellator.draw();
	}

	public static int drawBorder(Area area, int color)
	{
		if (!McLib.enableBorders.get())
		{
			area.draw(color);

			return 0;
		}

		area.draw(0xff000000);
		area.draw(color, 1);

		return 1;
	}

	public static void drawOutlineCenter(int x, int y, int offset, int color)
	{
		drawOutlineCenter(x, y, offset, color, 1);
	}

	public static void drawOutlineCenter(int x, int y, int offset, int color, int border)
	{
		drawOutline(x - offset, y - offset, x + offset, y + offset, color, border);
	}

	public static void drawOutline(int left, int top, int right, int bottom, int color)
	{
		drawOutline(left, top, right, bottom, color, 1);
	}

	/**
	 * Draw rectangle outline with given border
	 */
	public static void drawOutline(int left, int top, int right, int bottom, int color, int border)
	{
		Gui.drawRect(left, top, left + border, bottom, color);
		Gui.drawRect(right - border, top, right, bottom, color);
		Gui.drawRect(left + border, top, right - border, top + border, color);
		Gui.drawRect(left + border, bottom - border, right - border, bottom, color);
	}

	public static void drawOutlinedIcon(Icon icon, int x, int y, int color)
	{
		drawOutlinedIcon(icon, x, y, color, 0F, 0F);
	}

	/**
	 * Draw an icon with a black outline
	 */
	public static void drawOutlinedIcon(Icon icon, int x, int y, int color, float ax, float ay)
	{
		GlStateManager.color(0, 0, 0, 1);
		icon.render(x - 1, y, ax, ay);
		icon.render(x + 1, y, ax, ay);
		icon.render(x, y - 1, ax, ay);
		icon.render(x, y + 1, ax, ay);
		ColorUtils.bindColor(color);
		icon.render(x, y, ax, ay);
	}

	public static void drawLockedArea(GuiElement element)
	{
		drawLockedArea(element, 0);
	}

	/**
	 * Generic method for drawing locked (disabled) state of
	 * an input field
	 */
	public static void drawLockedArea(GuiElement element, int padding)
	{
		if (!element.isEnabled())
		{
			element.area.draw(0x88000000, padding);

			GuiDraw.drawOutlinedIcon(Icons.LOCKED, element.area.mx(), element.area.my(), 0xffffffff, 0.5F, 0.5F);
		}
	}

	public static void drawDropShadow(int left, int top, int right, int bottom, int offset, int opaque, int shadow)
	{
		float a1 = (opaque >> 24 & 255) / 255.0F;
		float r1 = (opaque >> 16 & 255) / 255.0F;
		float g1 = (opaque >> 8 & 255) / 255.0F;
		float b1 = (opaque & 255) / 255.0F;
		float a2 = (shadow >> 24 & 255) / 255.0F;
		float r2 = (shadow >> 16 & 255) / 255.0F;
		float g2 = (shadow >> 8 & 255) / 255.0F;
		float b2 = (shadow & 255) / 255.0F;

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		/* Draw opaque part */
		buffer.pos(right - offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
		buffer.pos(left + offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
		buffer.pos(left + offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();
		buffer.pos(right - offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();

		/* Draw top shadow */
		buffer.pos(right, top, 0).color(r2, g2, b2, a2).endVertex();
		buffer.pos(left, top, 0).color(r2, g2, b2, a2).endVertex();
		buffer.pos(left + offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
		buffer.pos(right - offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();

		/* Draw bottom shadow */
		buffer.pos(right - offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();
		buffer.pos(left + offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();
		buffer.pos(left, bottom, 0).color(r2, g2, b2, a2).endVertex();
		buffer.pos(right, bottom, 0).color(r2, g2, b2, a2).endVertex();

		/* Draw left shadow */
		buffer.pos(left + offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
		buffer.pos(left, top, 0).color(r2, g2, b2, a2).endVertex();
		buffer.pos(left, bottom, 0).color(r2, g2, b2, a2).endVertex();
		buffer.pos(left + offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();

		/* Draw right shadow */
		buffer.pos(right, top, 0).color(r2, g2, b2, a2).endVertex();
		buffer.pos(right - offset, top + offset, 0).color(r1, g1, b1, a1).endVertex();
		buffer.pos(right - offset, bottom - offset, 0).color(r1, g1, b1, a1).endVertex();
		buffer.pos(right, bottom, 0).color(r2, g2, b2, a2).endVertex();

		tessellator.draw();

		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static void drawDropCircleShadow(int x, int y, int radius, int segments, int opaque, int shadow)
	{
		float a1 = (opaque >> 24 & 255) / 255.0F;
		float r1 = (opaque >> 16 & 255) / 255.0F;
		float g1 = (opaque >> 8 & 255) / 255.0F;
		float b1 = (opaque & 255) / 255.0F;
		float a2 = (shadow >> 24 & 255) / 255.0F;
		float r2 = (shadow >> 16 & 255) / 255.0F;
		float g2 = (shadow >> 8 & 255) / 255.0F;
		float b2 = (shadow & 255) / 255.0F;

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);

		buffer.pos(x, y, 0).color(r1, g1, b1, a1).endVertex();

		for (int i = 0; i <= segments; i ++)
		{
			double a = i / (double) segments * Math.PI * 2 - Math.PI / 2;

			buffer.pos(x - Math.cos(a) * radius, y + Math.sin(a) * radius, 0).color(r2, g2, b2, a2).endVertex();
		}

		tessellator.draw();

		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static void drawDropCircleShadow(int x, int y, int radius, int offset, int segments, int opaque, int shadow)
	{
		if (offset >= radius)
		{
			drawDropCircleShadow(x, y, radius, segments, opaque, shadow);

			return;
		}

		float a1 = (opaque >> 24 & 255) / 255.0F;
		float r1 = (opaque >> 16 & 255) / 255.0F;
		float g1 = (opaque >> 8 & 255) / 255.0F;
		float b1 = (opaque & 255) / 255.0F;
		float a2 = (shadow >> 24 & 255) / 255.0F;
		float r2 = (shadow >> 16 & 255) / 255.0F;
		float g2 = (shadow >> 8 & 255) / 255.0F;
		float b2 = (shadow & 255) / 255.0F;

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		/* Draw opaque base */
		buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(x, y, 0).color(r1, g1, b1, a1).endVertex();

		for (int i = 0; i <= segments; i ++)
		{
			double a = i / (double) segments * Math.PI * 2 - Math.PI / 2;

			buffer.pos(x - Math.cos(a) * offset, y + Math.sin(a) * offset, 0).color(r1, g1, b1, a1).endVertex();
		}

		tessellator.draw();

		/* Draw outer shadow */
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		for (int i = 0; i < segments; i ++)
		{
			double alpha1 = i / (double) segments * Math.PI * 2 - Math.PI / 2;
			double alpha2 = (i + 1) / (double) segments * Math.PI * 2 - Math.PI / 2;

			buffer.pos(x - Math.cos(alpha2) * offset, y + Math.sin(alpha2) * offset, 0).color(r1, g1, b1, a1).endVertex();
			buffer.pos(x - Math.cos(alpha1) * offset, y + Math.sin(alpha1) * offset, 0).color(r1, g1, b1, a1).endVertex();
			buffer.pos(x - Math.cos(alpha1) * radius, y + Math.sin(alpha1) * radius, 0).color(r2, g2, b2, a2).endVertex();
			buffer.pos(x - Math.cos(alpha2) * radius, y + Math.sin(alpha2) * radius, 0).color(r2, g2, b2, a2).endVertex();
		}

		tessellator.draw();

		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	public static void drawMultiText(FontRenderer font, String text, int x, int y, int color, int width, int lineHeight, float ax, float ay)
	{
		List<String> list = font.listFormattedStringToWidth(text, width);
		int h = (lineHeight * (list.size() - 1)) + font.FONT_HEIGHT;

		y -= h * ay;

		for (String string : list)
		{
			font.drawStringWithShadow(string, x + (width - font.getStringWidth(string)) * ax, y, color);

			y += lineHeight;
		}
	}

	public static void drawTextBackground(FontRenderer font, String text, int x, int y, int color, int background)
	{
		int a = background >> 24 & 0xff;

		if (a != 0)
		{
			Gui.drawRect(x - 3, y - 3, x + font.getStringWidth(text) + 3, y + font.FONT_HEIGHT + 3, background);
		}

		font.drawStringWithShadow(text, x, y, color);
	}
}