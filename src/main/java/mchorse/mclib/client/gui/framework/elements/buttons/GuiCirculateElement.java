package mchorse.mclib.client.gui.framework.elements.buttons;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuiCirculateElement extends GuiButtonElement
{
	protected List<String> labels = new ArrayList<String>();
	protected int value = 0;

	public GuiCirculateElement(Minecraft mc, Consumer<GuiButtonElement> callback)
	{
		super(mc, "", callback);
	}

	public void addLabel(String label)
	{
		if (this.labels.isEmpty())
		{
			this.label = label;
		}

		this.labels.add(label);
	}

	public int getValue()
	{
		return this.value;
	}

	public String getLabel()
	{
		return this.labels.get(this.value);
	}

	public void setValue(int value)
	{
		this.value = value;

		if (this.value > this.labels.size() - 1)
		{
			this.value = 0;
		}

		if (this.value < 0)
		{
			this.value = this.labels.size() - 1;
		}

		this.label = this.labels.get(this.value);
	}

	public void toggle()
	{
		this.setValue(this.value + 1);
	}

	@Override
	protected void click()
	{
		this.toggle();
	}
}
