package mchorse.mclib.utils.resources;

import mchorse.mclib.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

public class TextureProcessor
{
	public static BufferedImage process(MultiResourceLocation multi)
	{
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		List<BufferedImage> images = new ArrayList<BufferedImage>();

		int w = 0;
		int h = 0;

		for (int i = 0; i < multi.children.size(); i++)
		{
			FilteredResourceLocation child = multi.children.get(i);

			try
			{
				IResource resource = manager.getResource(child.path);
				BufferedImage image = ImageIO.read(resource.getInputStream());

				images.add(image);

				w = Math.max(w, image.getWidth());
				h = Math.max(h, image.getHeight());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		BufferedImage image = new BufferedImage(w, h, images.get(0).getType());
		Graphics g = image.getGraphics();

		for (int i = 0; i < multi.children.size(); i++)
		{
			BufferedImage child = images.get(i);
			FilteredResourceLocation filter = multi.children.get(i);
			int iw = child.getWidth();
			int ih = child.getHeight();

			if (filter.scaleToLargest)
			{
				iw = w;
				ih = h;
			}
			else if (filter.scale != 0 && filter.scale > 0)
			{
				iw = (int) (iw * filter.scale);
				ih = (int) (ih * filter.scale);
			}

			if (iw > 0 && ih > 0)
			{
				if (filter.color != 0xffffff || filter.pixelate > 1)
				{
					processImage(child, filter);
				}

				g.drawImage(child, filter.shiftX, filter.shiftY, iw, ih, null);
			}
		}

		g.dispose();

		return image;
	}

	/**
	 * Apply filters
	 */
	private static void processImage(BufferedImage child, FilteredResourceLocation frl)
	{
		byte[] pixels = ((DataBufferByte) child.getRaster().getDataBuffer()).getData();
		int pixelLength = child.getAlphaRaster() != null ? 4 : 3;
		int width = child.getWidth();
		Color filter = new Color().set(frl.color, false);
		float r, g, b;

		for (int i = 0, c = pixels.length / pixelLength; i < c; i++)
		{
			int offset = 0;
			int index = i * pixelLength;

			if (pixelLength == 4)
			{
				if (((int) pixels[index + offset] & 0xff) == 0)
				{
					continue;
				}

				offset += 1;
			}

			if (frl.pixelate > 1)
			{
				int x = i % width;
				int y = i / width;
				boolean origin = x % frl.pixelate == 0 && y % frl.pixelate == 0;

				x = x - x % frl.pixelate;
				y = y - y % frl.pixelate;
				int target = (y * width + x) * pixelLength;

				pixels[index] = pixels[target];
				pixels[index + 1] = pixels[target + 1];
				pixels[index + 2] = pixels[target + 2];

				if (pixelLength == 4)
				{
					pixels[index + 3] = pixels[target + 3];
				}

				if (!origin)
				{
					continue;
				}
			}

			b = ((int) pixels[index + offset++] & 0xff) / 255F * filter.b;
			g = ((int) pixels[index + offset++] & 0xff) / 255F * filter.g;
			r = ((int) pixels[index + offset] & 0xff) / 255F * filter.r;
			offset = 0;

			if (pixelLength == 4)
			{
				offset += 1;
			}

			pixels[index + offset++] = (byte) (b * 0xff);
			pixels[index + offset++] = (byte) (g * 0xff);
			pixels[index + offset] = (byte) (r * 0xff);
		}
	}
}