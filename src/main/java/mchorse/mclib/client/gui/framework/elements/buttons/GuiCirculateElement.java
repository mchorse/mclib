package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class GuiCirculateElement extends GuiButtonElement
{
    protected List<IKey> labels = new ArrayList<IKey>();
    protected Set<Integer> disabled = new HashSet<Integer>();
    protected int value = 0;

    public GuiCirculateElement(Minecraft mc, Consumer<GuiButtonElement> callback)
    {
        super(mc, IKey.EMPTY, callback);
    }

    public List<IKey> getLabels()
    {
        return this.labels;
    }

    public void addLabel(IKey label)
    {
        if (this.labels.isEmpty())
        {
            this.label = label;
        }

        this.labels.add(label);
    }

    public void disable(int value)
    {
        if (this.disabled.size() < this.labels.size())
        {
            this.disabled.add(value);
        }
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
        this.setValue(value, 1);
    }

    public void setValue(int value, int direction)
    {
        this.value = value;

        if (this.disabled.contains(value))
        {
            this.setValue(value + direction, direction);

            return;
        }

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
        int direction = mouseButton == 0 ? 1 : -1;

        this.setValue(this.value + direction, direction);

        super.click(mouseButton);
    }
}
