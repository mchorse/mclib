package mchorse.mclib.client.gui.framework.elements.list;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.utils.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GuiLabelListElement <T> extends GuiListElement<Label<T>>
{
	public GuiLabelListElement(Minecraft mc, Consumer<List<Label<T>>> callback)
	{
		super(mc, callback);

		this.scroll.scrollItemSize = 16;
	}

	public void add(String title, T value)
	{
		this.add(new Label<T>(title, value));
	}

	public void setCurrentTitle(String title)
	{
		for (int i = 0; i < this.list.size(); i ++)
		{
			if (this.list.get(i).title.equals(title))
			{
				this.setIndex(i);

				return;
			}
		}
	}

	public void setCurrentValue(T value)
	{
		for (int i = 0; i < this.list.size(); i ++)
		{
			if (this.list.get(i).value.equals(value))
			{
				this.setIndex(i);

				return;
			}
		}
	}

	@Override
	protected boolean sortElements()
	{
		Collections.sort(this.list, (a, b) -> a.title.compareToIgnoreCase(b.title));

		return true;
	}

	@Override
	protected String elementToString(Label<T> element)
	{
		return element.title;
	}
}