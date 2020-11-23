package mchorse.mclib.utils.wav;

import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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

	private List<WaveformSprite> sprites = new ArrayList<WaveformSprite>();
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

		int maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE) / 2;
		int count = (int) Math.ceil(this.w / (double) maxTextureSize);
		int offset = 0;

		for (int t = 0; t < count; t++)
		{
			int texture = GlStateManager.generateTexture();
			int width = Math.min(this.w - offset, maxTextureSize);

			BufferedImage image = new BufferedImage(width, this.h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();

			for (int i = offset, j = 0, c = Math.min(offset + width, this.average.length); i < c; i++, j++)
			{
				float average = this.average[i];
				float maximum = this.maximum[i];

				int maxHeight = (int) (maximum * this.h);
				int avgHeight = (int) (average * (this.h - 1)) + 1;

				if (avgHeight > 0)
				{
					g.setColor(java.awt.Color.WHITE);
					g.drawRect(j, this.h / 2 - maxHeight / 2, 1, maxHeight);
					g.setColor(java.awt.Color.LIGHT_GRAY);
					g.drawRect(j, this.h / 2 - avgHeight / 2, 1, avgHeight);
				}
			}

			g.dispose();

			TextureUtil.uploadTextureImage(texture, image);
			GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);

			this.sprites.add(new WaveformSprite(texture, width));

			offset += maxTextureSize;
		}
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
		for (WaveformSprite sprite : this.sprites)
		{
			GlStateManager.deleteTexture(sprite.texture);
		}

		this.sprites.clear();
	}

	public boolean isCreated()
	{
		return !this.sprites.isEmpty();
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

	public List<WaveformSprite> getSprites()
	{
		return this.sprites;
	}

	public void draw(int x, int y, int u, int v, int w, int h)
	{
		draw(x, y, u, v, w, h, this.h);
	}

	/**
	 * Draw the waveform out of multiple sprites of desired cropped region
	 */
	public void draw(int x, int y, int u, int v, int w, int h, int height)
	{
		int offset = 0;

		for (WaveformSprite sprite : this.sprites)
		{
			int sw = sprite.width;
			offset += sw;

			if (w <= 0)
			{
				break;
			}

			if (u >= offset)
			{
				continue;
			}

			int so = offset - u;

			GlStateManager.bindTexture(sprite.texture);
			GuiDraw.drawBillboard(x, y, u, v, Math.min(w, so), h, sw, height);

			x += so;
			u += so;
			w -= so;
		}
	}

	@SideOnly(Side.CLIENT)
	public static class WaveformSprite
	{
		public final int texture;
		public final int width;

		public WaveformSprite(int texture, int width)
		{
			this.texture = texture;
			this.width = width;
		}
	}
}