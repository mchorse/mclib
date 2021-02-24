package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiKeybindElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.keys.KeyParser;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValueLong extends Value implements IServerValue
{
    private long value;
    private long defaultValue;
    private long min;
    private long max;

    private Long serverValue;

    public ValueLong(String id)
    {
        super(id);
    }

    public ValueLong(String id, long defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;

        this.reset();
    }

    public ValueLong(String id, long defaultValue, long min, long max)
    {
        super(id);

        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;

        this.reset();
    }

    public long getMin()
    {
        return this.min;
    }

    public long getMax()
    {
        return this.max;
    }

    public long get()
    {
        return this.serverValue == null ? this.value : this.serverValue;
    }

    public void set(long value)
    {
        this.value = MathUtils.clamp(value, this.min, this.max);
        this.saveLater();
    }

    @Override
    public Object getValue()
    {
        return this.get();
    }

    @Override
    public void setValue(Object value)
    {
        if (value instanceof Long)
        {
            this.set((Long) value);
        }
    }

    @Override
    public void reset()
    {
        this.set(this.defaultValue);
    }

    @Override
    public void resetServer()
    {
        this.serverValue = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiElement element = new GuiElement(mc);
        GuiLabel label = Elements.label(IKey.lang(this.getTitleKey()), 0).anchor(0, 0.5F);

        element.flex().row(0).preferred(0).height(20);
        element.add(label);

        GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);

        trackpad.flex().w(90);
        element.add(trackpad.removeTooltip());

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

    @Override
    public boolean parseFromCommand(String value)
    {
        try
        {
            this.set(Integer.parseInt(value));

            return true;
        }
        catch (Exception e)
        {}

        return false;
    }

    @Override
    public void copy(IConfigValue value)
    {
        if (value instanceof ValueLong)
        {
            this.value = ((ValueLong) value).value;
        }
    }

    @Override
    public void copyServer(IConfigValue value)
    {
        if (value instanceof ValueLong)
        {
            this.serverValue = ((ValueLong) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);

        this.value = buffer.readLong();
        this.defaultValue = buffer.readLong();
        this.min = buffer.readLong();
        this.max = buffer.readLong();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);

        buffer.writeLong(this.value);
        buffer.writeLong(this.defaultValue);
        buffer.writeLong(this.min);
        buffer.writeLong(this.max);
    }

    @Override
    public String toString()
    {
        return Long.toString(this.value);
    }
}