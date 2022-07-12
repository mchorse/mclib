package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class GuiCirculateElement extends GuiClickElement<GuiCirculateElement>
{
    public boolean custom;
    public int customColor;

    protected List<IKey> labels = new ArrayList<IKey>();
    protected Set<Integer> disabled = new HashSet<Integer>();
    protected int value = 0;

    public GuiCirculateElement(Minecraft mc, Consumer<GuiCirculateElement> callback)
    {
        super(mc, callback);

        this.flex().h(20);
    }

    public GuiCirculateElement color(int color)
    {
        this.custom = true;
        this.customColor = color & 0xffffff;

        return this;
    }

    public List<IKey> getLabels()
    {
        return this.labels;
    }

    public void addLabel(IKey label)
    {
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

    @Override
    protected GuiCirculateElement get()
    {
        return this;
    }

    @Override
    protected void drawSkin(GuiContext context)
    {
        int color = 0xff000000 + (this.custom ? this.customColor : McLib.primaryColor.get());

        if (this.hover)
        {
            color = ColorUtils.multiplyColor(color, 0.85F);
        }

        GuiDraw.drawBorder(this.area, color);

        String label = this.getLabel();
        int x = this.area.mx(this.font.getStringWidth(label));
        int y = this.area.my(this.font.FONT_HEIGHT - 1);

        this.font.drawStringWithShadow(label, x, y, this.hover ? 16777120 : 0xffffff);

        GuiDraw.drawLockedArea(this);
    }
}
