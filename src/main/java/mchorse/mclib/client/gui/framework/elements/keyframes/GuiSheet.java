package mchorse.mclib.client.gui.framework.elements.keyframes;

import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.keyframes.Keyframe;
import mchorse.mclib.utils.keyframes.KeyframeChannel;
import mchorse.mclib.utils.keyframes.KeyframeEasing;
import mchorse.mclib.utils.keyframes.KeyframeInterpolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiSheet
{
	public IKey title;
	public int color;
	public KeyframeChannel channel;
	public List<Integer> selected = new ArrayList<Integer>();
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
		List<Keyframe> keyframes = new ArrayList<Keyframe>();

		for (int index : this.selected)
		{
			Keyframe keyframe = this.channel.get(index);

			if (keyframe != null)
			{
				keyframes.add(keyframe);
			}
		}

		this.channel.sort();
		this.selected.clear();

		for (Keyframe keyframe : keyframes)
		{
			this.selected.add(this.channel.getKeyframes().indexOf(keyframe));
		}
	}

	public void setInterpolation(KeyframeInterpolation interp)
	{
		for (int index : this.selected)
		{
			Keyframe keyframe = this.channel.get(index);

			if (keyframe != null)
			{
				keyframe.setInterpolation(interp);
			}
		}
	}

	public void setEasing(KeyframeEasing easing)
	{
		for (int index : this.selected)
		{
			Keyframe keyframe = this.channel.get(index);

			if (keyframe != null)
			{
				keyframe.setEasing(easing);
			}
		}
	}

	public Keyframe getKeyframe()
	{
		if (this.selected.isEmpty())
		{
			return null;
		}

		return this.channel.get(this.selected.get(0));
	}

	public boolean hasSelected(int i)
	{
		return this.selected.contains(i);
	}

	public void clearSelection()
	{
		this.selected.clear();
	}

	public int getSelectedCount()
	{
		return this.selected.size();
	}

	public void removeKeyframes()
	{
		List<Integer> sorted = new ArrayList<Integer>(this.selected);

		Collections.sort(sorted);
		Collections.reverse(sorted);

		this.clearSelection();

		for (int index : sorted)
		{
			this.channel.remove(index);
		}
	}
}
