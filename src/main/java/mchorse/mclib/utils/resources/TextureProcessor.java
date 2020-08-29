package mchorse.mclib.utils.resources;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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
			ResourceLocation child = multi.children.get(i);

			try
			{
				IResource resource = manager.getResource(child);
				BufferedImage image = ImageIO.read(resource.getInputStream());

				images.add(image);

				w = Math.max(w, image.getWidth());
				h = Math.max(h, image.getHeight());
			}
			catch (Exception e)
			{}
		}

		BufferedImage image = new BufferedImage(w, h, images.get(0).getType());
		Graphics g = image.getGraphics();

		for (int i = 0; i < multi.children.size(); i++)
		{
			ResourceLocation location = multi.children.get(i);
			BufferedImage child = images.get(i);

			if (location instanceof FilteredResourceLocation)
			{
				FilteredResourceLocation filter = (FilteredResourceLocation) location;

				if (filter.scaleToLargest)
				{
					g.drawImage(child, 0, 0, w, h, null);
				}
				else
				{
					g.drawImage(child, 0, 0, null);
				}
			}
			else
			{
				g.drawImage(child, 0, 0, null);
			}

		}

		g.dispose();

		return image;
	}
}