package mchorse.mclib.utils;

import net.minecraft.client.renderer.GlStateManager;

public class ColorUtils
{
	private static final Color COLOR = new Color();

	public static int multiplyColor(int color, float factor)
	{
		COLOR.set(color, true);
		COLOR.r *= factor;
		COLOR.g *= factor;
		COLOR.b *= factor;

		return COLOR.getRGBAColor();
	}

	public static int setAlpha(int color, float alpha)
	{
		COLOR.set(color, true);
		COLOR.a = alpha;

		return COLOR.getRGBAColor();
	}

	public static void bindColor(int color)
	{
		COLOR.set(color, true);

		GlStateManager.color(COLOR.r, COLOR.g, COLOR.b, COLOR.a);
	}

	public static int rgbaToInt(float r, float g, float b, float a)
	{
		COLOR.set(r, g, b, a);

		return COLOR.getRGBAColor();
	}

	public static int parseColor(String color)
	{
		return parseColor(color, 0);
	}

	public static int parseColor(String color, int orDefault)
	{
		if (color.startsWith("#"))
		{
			color = color.substring(1);
		}

		if (color.length() == 6 || color.length() == 8)
		{
			try
			{
				if (color.length() == 8)
				{
					String alpha = color.substring(0, 2);
					String rest = color.substring(2);

					int a = Integer.parseInt(alpha, 16) << 24;
					int rgb = Integer.parseInt(rest, 16);

					return a + rgb;
				}

				return Integer.parseInt(color, 16);
			}
			catch (Exception e)
			{}
		}

		return orDefault;
	}
}