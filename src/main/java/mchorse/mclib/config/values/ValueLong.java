package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueLong extends GenericNumberValue<Long> implements IServerValue, IConfigGuiProvider
{
    public ValueLong(String id)
    {
        super(id, 0L, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public ValueLong(String id, long defaultValue)
    {
        super(id, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public ValueLong(String id, long defaultValue, long min, long max)
    {
        super(id, defaultValue, min, max);
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
    public void resetServer()
    {
        this.serverValue = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiElement element = new GuiElement(mc);
        GuiLabel label = Elements.label(IKey.lang(this.getLabelKey()), 0).anchor(0, 0.5F);

        element.flex().row(0).preferred(0).height(20);
        element.add(label);

        GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);

        trackpad.flex().w(90);
        element.add(trackpad.removeTooltip());

        return Arrays.asList(element.tooltip(IKey.lang(this.getCommentKey())));
    }

    @Override
    public void valueFromJSON(JsonElement element)
    {
        this.set(element.getAsLong());
    }

    @Override
    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value);
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        try
        {
            this.set(Long.parseLong(value));

            return true;
        }
        catch (Exception e)
        {}

        return false;
    }

    @Override
    public void copy(Value value)
    {
        if (value instanceof ValueLong)
        {
            this.value = ((ValueLong) value).value;
        }
    }

    @Override
    public void copyServer(Value value)
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