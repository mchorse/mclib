package mchorse.mclib.utils;

import net.minecraft.client.renderer.GlStateManager;

public class ColorUtils
{
	public static int multiplyColor(int color, float factor)
	{
		float r = ((color >> 16) & 0xff) / 255F;
		float g = ((color >> 8) & 0xff) / 255F;
		float b = ((color >> 0) & 0xff) / 255F;
		float a = ((color >> 24) & 0xff) / 255F;

		r *= factor;
		g *= factor;
		b *= factor;

		return rgbaToInt(MathUtils.clamp(r, 0, 1), MathUtils.clamp(g, 0, 1), MathUtils.clamp(b, 0, 1), a);
	}

	public static void bindColor(int color)
	{
		float r = ((color >> 16) & 0xff) / 255F;
		float g = ((color >> 8) & 0xff) / 255F;
		float b = ((color >> 0) & 0xff) / 255F;
		float a = ((color >> 24) & 0xff) / 255F;

		GlStateManager.color(r, g, b, a);
	}

	public static int rgbaToInt(float r, float g, float b, float a)
	{
		r = MathUtils.clamp(r, 0, 1);
		g = MathUtils.clamp(g, 0, 1);
		b = MathUtils.clamp(b, 0, 1);
		a = MathUtils.clamp(a, 0, 1);

		return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
	}

	public static int parseColor(String color)
	{
		return parseColor(color, 0);
	}

	public static int parseColor(String color, int orDefault)
	{
		if (color.startsWith("#") && (color.length() == 7 || color.length() == 9))
		{
			try
			{
				return Integer.parseInt(color.substring(1), 16);
			}
			catch (Exception e) {}
		}

		return orDefault;
	}
}