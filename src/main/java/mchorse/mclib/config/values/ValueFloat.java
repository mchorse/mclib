package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueFloat extends Value
{
    private float value;
    private float defaultValue;
    public final float min;
    public final float max;

    public ValueFloat(String id, float defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;
        this.min = Float.NEGATIVE_INFINITY;
        this.max = Float.POSITIVE_INFINITY;

        this.reset();
    }

    public ValueFloat(String id, float defaultValue, float min, float max)
    {
        super(id);

        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;

        this.reset();
    }

    public float get()
    {
        return this.value;
    }

    public void set(float value)
    {
        this.value = MathUtils.clamp(value, this.min, this.max);
        this.saveLater();
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
        GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);

        trackpad.flex().w(90);

        element.flex().row(0).preferred(0).height(20);
        element.add(label, trackpad.removeTooltip());

        return Arrays.asList(element.tooltip(IKey.lang(this.getTooltipKey())));
    }

    @Override
    public void fromJSON(JsonElement element)
    {
        this.set(element.getAsFloat());
    }

    @Override
    public JsonElement toJSON()
    {
        return new JsonPrimitive(this.value);
    }
}