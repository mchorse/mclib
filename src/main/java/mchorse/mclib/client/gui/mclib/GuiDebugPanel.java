package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.client.gui.framework.elements.keyframes.GuiDopeSheet;
import mchorse.mclib.client.gui.framework.elements.keyframes.GuiGraphView;
import mchorse.mclib.client.gui.framework.elements.keyframes.GuiKeyframesEditor;
import mchorse.mclib.client.gui.framework.elements.keyframes.GuiSheet;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.keyframes.Keyframe;
import mchorse.mclib.utils.keyframes.KeyframeChannel;
import mchorse.mclib.utils.keyframes.KeyframeInterpolation;
import net.minecraft.client.Minecraft;

public class GuiDebugPanel extends GuiDashboardPanel
{
	public GuiKeyframesEditor<GuiDopeSheet> dopesheet;
	public GuiKeyframesEditor<GuiGraphView> graph;

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
		this.add(this.dopesheet, this.graph);
	}

	@Override
	public void resize()
	{
		super.resize();

		this.dopesheet.graph.resetView();
		this.graph.graph.resetView();
	}

	@Override
	public boolean needsBackground()
	{
		return false;
	}
}