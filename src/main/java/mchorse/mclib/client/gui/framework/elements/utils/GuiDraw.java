package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.utils.Area;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class GuiDraw
{
	public static void scissor(int x, int y, int w, int h, GuiContext context)
	{
	    scissor(x, y, w, h, context.screen.width, context.screen.height);
	}

	/**
	 * Scissor (clip) the screen
	 */
	public static void scissor(int x, int y, int w, int h, int sw, int sh)
	{
	    Minecraft mc = Minecraft.getMinecraft();

	    /* F*$! those ints */
	    float rx = (float) Math.ceil((double) mc.displayWidth / (double) sw);
	    float ry = (float) Math.ceil((double) mc.displayHeight / (double) sh);

	    /* Clipping area around scroll area */
	    int xx = (int) (x * rx);
	    int yy = (int) (mc.displayHeight - (y + h) * ry);
	    int ww = (int) (w * rx);
	    int hh = (int) (h * ry);

	    GL11.glScissor(xx, yy, ww, hh);
	    GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}

	public static void unscissor()
	{
	    GL11.glDisable(GL11.GL_SCISSOR_TEST);
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
	    GlStateManager.shadeModel(7425);

	    Tessellator tessellator = Tessellator.getInstance();
	    VertexBuffer vertexbuffer = tessellator.getBuffer();
	    vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
	    vertexbuffer.pos(right, top, zLevel).color(r2, g2, b2, a2).endVertex();
	    vertexbuffer.pos(left, top, zLevel).color(r1, g1, b1, a1).endVertex();
	    vertexbuffer.pos(left, bottom, zLevel).color(r1, g1, b1, a1).endVertex();
	    vertexbuffer.pos(right, bottom, zLevel).color(r2, g2, b2, a2).endVertex();
	    tessellator.draw();

	    GlStateManager.shadeModel(7424);
	    GlStateManager.disableBlend();
	    GlStateManager.enableAlpha();
	    GlStateManager.enableTexture2D();
	}

	public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH)
	{
	    drawBillboard(x, y, u, v, w, h, textureW, textureH, 0);
	}

	public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH, float z)
	{
	    float tw = 1F / textureW;
	    float th = 1F / textureH;

	    Tessellator tessellator = Tessellator.getInstance();
	    VertexBuffer vertexbuffer = tessellator.getBuffer();

	    vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	    vertexbuffer.pos(x, y + h, z).tex(u * tw, (v + h) * th).endVertex();
	    vertexbuffer.pos(x + w, y + h, z).tex((u + w) * tw, (v + h) * th).endVertex();
	    vertexbuffer.pos(x + w, y, z).tex((u + w) * tw, v * th).endVertex();
	    vertexbuffer.pos(x, y, z).tex(u * tw, v * th).endVertex();

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

	public static void drawOutline(int left, int top, int right, int bottom, int color)
	{
		drawOutline(left, top, right, bottom, color, 1);
	}

	public static void drawOutline(int left, int top, int right, int bottom, int color, int border)
	{
		Gui.drawRect(left, top, left + border, bottom, color);
		Gui.drawRect(right - border, top, right, bottom, color);
		Gui.drawRect(left + border, top, right - border, top + border, color);
		Gui.drawRect(left + border, bottom - border, right - border, bottom, color);
	}
}