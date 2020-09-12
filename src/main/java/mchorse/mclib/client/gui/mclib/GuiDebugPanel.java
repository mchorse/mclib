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
import mchorse.mclib.utils.wav.WavePlayer;
import mchorse.mclib.utils.wav.WaveReader;
import mchorse.mclib.utils.wav.Waveform;
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

	private WavePlayer player;
	private Waveform wave;

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

			if (data.getBytesPerSample() > 2)
			{
				data = data.convertTo16();
			}

			this.player = new WavePlayer().initialize(data);
			this.player.play();

			this.wave = new Waveform();
			this.wave.generate(data, 800, 300);
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
		if (this.player != null && !this.player.isPlaying())
		{
			this.player.delete();
			this.player = null;
		}

		super.draw(context);

		if (this.wave != null)
		{
			int w = this.wave.getWidth();
			int h = this.wave.getHeight();

			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableTexture2D();
			GlStateManager.bindTexture(this.wave.getTexture());
			GuiDraw.drawBillboard(this.area.x + 10, this.area.my() - h / 2, 0, 0, w, h, w, h);
		}
	}
}