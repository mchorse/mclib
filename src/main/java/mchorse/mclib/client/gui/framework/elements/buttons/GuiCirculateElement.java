package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuiCirculateElement extends GuiButtonElement
{
	protected List<IKey> labels = new ArrayList<IKey>();
	protected int value = 0;

	public GuiCirculateElement(Minecraft mc, Consumer<GuiButtonElement> callback)
	{
		super(mc, IKey.EMPTY, callback);
	}

	public void addLabel(IKey label)
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
		return this.labels.get(this.value).get();
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

	@Override
	protected boolean isAllowed(int mouseButton)
	{
		return mouseButton == 0 || mouseButton == 1;
	}

	@Override
	protected void click(int mouseButton)
	{
		this.setValue(this.value + (mouseButton == 0 ? 1 : -1));

		super.click(mouseButton);
	}
}
