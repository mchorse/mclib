package mchorse.mclib.utils.wav;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Waveform class
 *
 * This class allows to
 */
@SideOnly(Side.CLIENT)
public class Waveform
{
	public float[] average;
	public float[] maximum;

	private int texture = -1;
	private int w;
	private int h;
	private int pixelsPerSecond;

	public void generate(Wave data, int pixelsPerSecond, int height)
	{
		if (data.getBytesPerSample() != 2)
		{
			throw new IllegalStateException("Waveform generation doesn't support non 16-bit audio data!");
		}

		this.populate(data, pixelsPerSecond, height);
		this.render();
	}

	public void render()
	{
		this.delete();
		this.texture = GlStateManager.generateTexture();

		BufferedImage image = new BufferedImage(this.w, this.h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();

		for (int i = 0; i < this.average.length; i++)
		{
			float average = this.average[i];
			float maximum = this.maximum[i];

			int maxHeight = (int) (maximum * this.h);
			int avgHeight = (int) (average * (this.h - 1)) + 1;

			if (avgHeight > 0)
			{
				g.setColor(java.awt.Color.WHITE);
				g.drawRect(i, this.h / 2 - maxHeight / 2, 1, maxHeight);
				g.setColor(java.awt.Color.LIGHT_GRAY);
				g.drawRect(i, this.h / 2 - avgHeight / 2, 1, avgHeight);
			}
		}

		g.dispose();

		TextureUtil.uploadTextureImage(this.texture, image);
	}

	public void populate(Wave data, int pixelsPerSecond, int height)
	{
		this.pixelsPerSecond = pixelsPerSecond;
		this.w = (int) (data.getDuration() * pixelsPerSecond);
		this.h = height;
		this.average = new float[this.w];
		this.maximum = new float[this.w];

		int region = data.getScanRegion(pixelsPerSecond);

		for (int i = 0; i < this.w; i ++)
		{
			int offset = i * region;
			int count = 0;
			float average = 0;
			float maximum = 0;

			for (int j = 0; j < region; j += 2 * data.numChannels)
			{
				if (offset + j + 1 >= data.data.length)
				{
					break;
				}

				byte a = data.data[offset + j];
				byte b = data.data[offset + j + 1];
				float sample = a + (b << 8);

				maximum = Math.max(maximum, Math.abs(sample));
				average += Math.abs(sample);
				count++;
			}

			average /= count;
			average /= 0xffff / 2;
			maximum /= 0xffff / 2;

			this.average[i] = average;
			this.maximum[i] = maximum;
		}
	}

	public void delete()
	{
		GlStateManager.deleteTexture(this.texture);

		this.texture = -1;
	}

	public boolean isCreated()
	{
		return this.texture != -1;
	}

	public int getTexture()
	{
		return this.texture;
	}

	public int getPixelsPerSecond()
	{
		return this.pixelsPerSecond;
	}

	public int getWidth()
	{
		return this.w;
	}

	public int getHeight()
	{
		return this.h;
	}
}