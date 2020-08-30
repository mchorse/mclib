package mchorse.mclib.utils.resources;

import mchorse.mclib.utils.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Stack;

public class MultiskinThread implements Runnable
{
	private static MultiskinThread instance;

	public Stack<MultiResourceLocation> locations = new Stack<MultiResourceLocation>();

	public static synchronized void add(MultiResourceLocation location)
	{
		if (instance != null && instance.locations.isEmpty())
		{
			instance = null;
		}

		if (instance == null)
		{
			instance = new MultiskinThread();
			instance.addLocation(location);
			new Thread(instance).start();
		}
		else
		{
			instance.addLocation(location);
		}
	}

	public void addLocation(MultiResourceLocation location)
	{
		this.locations.add(location);
	}

	/**
	 * Create a byte buffer from buffered image
	 */
	public static ByteBuffer bytesFromBuffer(BufferedImage image)
	{
		int w = image.getWidth();
		int h = image.getHeight();

		ByteBuffer buffer = GLAllocation.createDirectByteBuffer(w * h * 4);
		int[] pixels = new int[w * h];

		image.getRGB(0, 0, w, h, pixels, 0, w);

		for (int y = 0; y < h; y++)
		{
			for (int x = 0; x < w; x++)
			{
				int pixel = pixels[y * w + x];

				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();

		return buffer;
	}

	@Override
	public void run()
	{
		while (!this.locations.isEmpty())
		{
			try
			{
				Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(Minecraft.getMinecraft().renderEngine);
				MultiResourceLocation location = this.locations.pop();
				ITextureObject texture = map.get(location);

				if (texture != null)
				{
					BufferedImage image = TextureProcessor.process(location);
					int w = image.getWidth();
					int h = image.getHeight();
					ByteBuffer buffer = bytesFromBuffer(image);

					Minecraft.getMinecraft().addScheduledTask(() ->
					{
						TextureUtil.allocateTexture(texture.getGlTextureId(), w, h);

						GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
					});
				}
				else
				{
					this.locations.add(location);
				}

				Thread.sleep(200);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		instance = null;
	}
}