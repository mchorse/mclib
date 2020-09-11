package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.keyframes.GuiDopeSheet;
import mchorse.mclib.client.gui.framework.elements.keyframes.GuiGraphView;
import mchorse.mclib.client.gui.framework.elements.keyframes.GuiKeyframesEditor;
import mchorse.mclib.client.gui.framework.elements.keyframes.GuiSheet;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.keyframes.Keyframe;
import mchorse.mclib.utils.keyframes.KeyframeChannel;
import mchorse.mclib.utils.keyframes.KeyframeInterpolation;
import mchorse.mclib.utils.wav.Wave;
import mchorse.mclib.utils.wav.WaveReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class GuiDebugPanel extends GuiDashboardPanel
{
	public GuiKeyframesEditor<GuiDopeSheet> dopesheet;
	public GuiKeyframesEditor<GuiGraphView> graph;
	public GuiModelRenderer renderer;
	public GuiButtonElement play;

	private int al = -1;
	private int als = -1;
	private int texture = -1;

	private int w;
	private int h;

	public GuiDebugPanel(Minecraft mc, GuiDashboard dashboard)
	{
		super(mc, dashboard);

		this.dopesheet = new GuiKeyframesEditor<GuiDopeSheet>(mc)
		{
			@Override
			protected GuiDopeSheet createElement(Minecraft mc)
			{
				return new GuiDopeSheet(mc, this::fillData);
			}
		};

		this.graph = new GuiKeyframesEditor<GuiGraphView>(mc)
		{
			@Override
			protected GuiGraphView createElement(Minecraft mc)
			{
				return new GuiGraphView(mc, this::fillData);
			}
		};

		KeyframeChannel channel = new KeyframeChannel();

		channel.insert(0, 10);
		Keyframe a = channel.get(channel.insert(20, 10));
		channel.get(channel.insert(80, 0));
		channel.get(channel.insert(100, 0));

		a.interp = KeyframeInterpolation.BEZIER;

		for (int i = 0; i < 5; i++)
		{
			KeyframeChannel c = new KeyframeChannel();

			c.copy(channel);

			this.dopesheet.graph.sheets.add(new GuiSheet(IKey.str("Test " + i), new Color((float) Math.random(), (float) Math.random(), (float) Math.random()).getRGBColor(), c));
		}

		this.dopesheet.graph.duration = 100;
		this.graph.graph.setChannel(channel, 0x0088ff);
		this.graph.graph.duration = 100;

		this.dopesheet.flex().relative(this).y(0).wh(1F, 0.5F);
		this.graph.flex().relative(this).y(0.5F).wh(1F, 0.5F);
		// this.add(this.dopesheet, this.graph);

		this.renderer = new GuiModelRenderer(mc)
		{
			@Override
			protected void drawUserModel(GuiContext context)
			{}
		};
		this.play = new GuiButtonElement(mc, IKey.str("Play me!"), this::play);

		this.renderer.flex().relative(this).wh(1F, 1F);
		this.play.flex().relative(this).xy(10, 10).w(80);
		this.add(this.renderer, this.play);
	}

	private void play(GuiButtonElement button)
	{
		try
		{
			WaveReader reader = new WaveReader();
			Wave data = reader.read(this.getClass().getResourceAsStream("/assets/mclib/hammer.wav"));

			this.al = AL10.alGenBuffers();
			ByteBuffer buffer = GLAllocation.createDirectByteBuffer(data.data.length);

			buffer.put(data.data);
			buffer.flip();

			AL10.alBufferData(this.al, AL10.AL_FORMAT_MONO16, buffer, data.sampleRate);

			this.als = AL10.alGenSources();
			AL10.alSourcei(this.als, AL10.AL_BUFFER, this.al);
			AL10.alSourcei(this.als, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
			AL10.alSourcePlay(this.als);

			GlStateManager.deleteTexture(this.texture);

			this.texture = GL11.glGenTextures();

			float pixelsPerSecond = 800;

			BufferedImage image = new BufferedImage((int) (data.getDuration() * pixelsPerSecond), 300, BufferedImage.TYPE_INT_ARGB);
			Graphics g = image.getGraphics();

			this.w = image.getWidth();
			this.h = image.getHeight();
			int region = (int) (data.sampleRate / pixelsPerSecond) * 2;
			int offset = 0;

			for (int i = 0; i < this.w; i ++)
			{
				offset = i * region;
				int count = 0;
				float avg = 0;
				float max = 0;

				for (int j = 0; j < region; j += 2)
				{
					if (offset + j + 1 >= data.data.length)
					{
						System.out.println("Whoops...");

						break;
					}

					byte a = data.data[offset + j];
					byte b = data.data[offset + j + 1];

					int hmm = a + (b << 8);

					max = Math.max(max, Math.abs(hmm));
					avg += Math.abs(hmm);
					count++;
				}

				avg /= count;
				avg /= 0xffff / 2F;
				max /= 0xffff / 2F;

				int maxHeight = (int) (max * this.h);
				int height = (int) (avg * (this.h - 1)) + 1;

				if (height > 0)
				{
					g.setColor(java.awt.Color.WHITE);
					g.drawRect(i, this.h / 2 - maxHeight / 2, 1, maxHeight);
					g.setColor(java.awt.Color.LIGHT_GRAY);
					g.drawRect(i, this.h / 2 - height / 2, 1, height);
				}
			}
			
			System.out.println(offset + " " + data.data.length);

			g.dispose();

			TextureUtil.uploadTextureImage(this.texture, image);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void resize()
	{
		super.resize();

		this.dopesheet.graph.resetView();
		this.graph.graph.resetView();
	}

	@Override
	public void draw(GuiContext context)
	{
		if (this.als != -1)
		{
			int playing = AL10.alGetSourcei(this.als, AL10.AL_SOURCE_STATE);

			if (playing != AL10.AL_PLAYING)
			{
				AL10.alDeleteBuffers(this.al);
				AL10.alDeleteSources(this.als);

				this.al = -1;
				this.als = -1;
			}
		}

		super.draw(context);

		if (this.texture != -1)
		{
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableTexture2D();
			GlStateManager.bindTexture(this.texture);
			GuiDraw.drawBillboard(this.area.x + 10, this.area.my() - this.h / 2, 0, 0, this.w, this.h, this.w, this.h);
		}
	}
}