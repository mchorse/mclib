package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiKeybindElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueInt extends Value
{
    private int value;
    private int defaultValue;
    public final int min;
    public final int max;
    private Subtype subtype = Subtype.INTEGER;
    private IKey[] labels;

    public ValueInt(String id, int defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;

        this.reset();
    }

    public ValueInt(String id, int defaultValue, int min, int max)
    {
        super(id);

        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;

        this.reset();
    }

    public int get()
    {
        return this.value;
    }

    public void set(int value)
    {
        this.value = MathUtils.clamp(value, this.min, this.max);
        this.saveLater();
    }

    public void setColorValue(String value)
    {
        this.set(ColorUtils.parseColor(value));
    }

    public Subtype getSubtype()
    {
        return this.subtype;
    }

    public ValueInt subtype(Subtype subtype)
    {
        this.subtype = subtype;

        return this;
    }

    public ValueInt color()
    {
        return this.subtype(Subtype.COLOR);
    }

    public ValueInt colorAlpha()
    {
        return this.subtype(Subtype.COLOR_ALPHA);
    }

    public ValueInt keybind()
    {
        return this.subtype(Subtype.KEYBIND);
    }

    public ValueInt modes(IKey... labels)
    {
        this.labels = labels;

        return this.subtype(Subtype.MODES);
    }

    @Override
    public void reset()
    {
        this.set(this.defaultValue);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfig gui)
    {
        GuiElement element = new GuiElement(mc);
        GuiLabel label = Elements.label(IKey.lang(this.getTitleKey()), 0).anchor(0, 0.5F);

        element.flex().row(0).preferred(0).height(20);
        element.add(label);

        if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
        {
            GuiColorElement color = new GuiColorElement(mc, this);

            color.flex().w(90);
            element.add(color.removeTooltip());
        }
        else if (this.subtype == Subtype.KEYBIND)
        {
            GuiKeybindElement keybind = new GuiKeybindElement(mc, this);

            keybind.flex().w(90);
            element.add(keybind.removeTooltip());
        }
        else if (this.subtype == Subtype.MODES)
        {
            GuiCirculateElement button = new GuiCirculateElement(mc, null);

            for (IKey key : this.labels)
            {
                button.addLabel(key);
            }

            button.callback = (b) -> this.set(button.getValue());
            button.setValue(this.get());
            button.flex().w(90);
            element.add(button);
        }
        else
        {
            GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);

            trackpad.flex().w(90);
            element.add(trackpad.removeTooltip());
        }

        return Arrays.asList(element.tooltip(IKey.lang(this.getTooltipKey())));
    }

    @Override
    public void fromJSON(JsonElement element)
    {
        this.set(element.getAsInt());
    }

    @Override
    public JsonElement toJSON()
    {
        return new JsonPrimitive(this.value);
    }

    public static enum Subtype
    {
        INTEGER,
        COLOR,
        COLOR_ALPHA,
        KEYBIND,
        MODES
    }
}