package mchorse.mclib.client.gui.framework.elements.list;

import mchorse.mclib.client.gui.utils.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.Collections;
import java.util.function.Consumer;

public class GuiLabelListElement <T> extends GuiListElement<Label<T>>
{
	public GuiLabelListElement(Minecraft mc, Consumer<Label<T>> callback)
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
				this.current = i;

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
				this.current = i;

				return;
			}
		}
	}

	public void sort()
	{
		Label current = this.getCurrent();

		Collections.sort(this.list, (a, b) -> a.title.compareToIgnoreCase(b.title));

		if (current != null)
		{
			this.setCurrent(current);
		}
	}

	@Override
	public void drawElement(Label<T> element, int i, int x, int y, boolean hover)
	{
		if (this.current == i)
		{
			Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x880088ff);
		}

		this.font.drawStringWithShadow(element.title, x + 4, y + 4, hover ? 16777120 : 0xffffff);
	}
}