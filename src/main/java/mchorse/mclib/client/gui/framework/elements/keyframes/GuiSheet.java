package mchorse.mclib.client.gui.framework.elements.keyframes;

import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.keyframes.Keyframe;
import mchorse.mclib.utils.keyframes.KeyframeChannel;
import mchorse.mclib.utils.keyframes.KeyframeEasing;
import mchorse.mclib.utils.keyframes.KeyframeInterpolation;

public class GuiSheet
{
	public IKey title;
	public int color;
	public KeyframeChannel channel;
	public int selected = -1;
	public boolean handles = true;

	public GuiSheet(IKey title, int color, KeyframeChannel channel, boolean handles)
	{
		this(title, color, channel);

		this.handles = handles;
	}

	public GuiSheet(IKey title, int color, KeyframeChannel channel)
	{
		this.title = title;
		this.color = color;
		this.channel = channel;
	}

	public void sort()
	{
		Keyframe frame = this.getKeyframe();

		this.channel.sort();
		this.selected = this.channel.getKeyframes().indexOf(frame);
	}

	public void setTick(double tick, Selection which)
	{
        Keyframe keyframe = this.getKeyframe();

        if (keyframe != null)
        {
	        which.setX(keyframe, tick);
        }
	}

	public void setValue(double value, Selection which)
	{
        Keyframe keyframe = this.getKeyframe();

        if (keyframe != null)
        {
	        which.setY(keyframe, value);
        }
	}

	public void setInterpolation(KeyframeInterpolation interp)
	{
        Keyframe keyframe = this.getKeyframe();

        if (keyframe != null)
        {
	        keyframe.setInterpolation(interp);
        }
	}

	public void setEasing(KeyframeEasing easing)
	{
		Keyframe keyframe = this.getKeyframe();

		if (keyframe != null)
		{
			keyframe.setEasing(easing);
		}
	}

	public Keyframe getKeyframe()
	{
		return this.channel.get(this.selected);
	}

	public void clearSelection()
	{
		this.selected = -1;
	}
}
