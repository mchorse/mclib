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

public class ValueFloat extends GenericNumberValue<Float> implements IServerValue, IConfigGuiProvider
{
    public ValueFloat(String id)
    {
        super(id, 0F, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public ValueFloat(String id, float defaultValue)
    {
        super(id, defaultValue, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public ValueFloat(String id, float defaultValue, float min, float max)
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
        if (value instanceof Float)
        {
            this.set((Float) value);
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
        GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);

        trackpad.flex().w(90);

        element.flex().row(0).preferred(0).height(20);
        element.add(label, trackpad.removeTooltip());

        return Arrays.asList(element.tooltip(IKey.lang(this.getCommentKey())));
    }

    @Override
    public void valueFromJSON(JsonElement element)
    {
        this.set(element.getAsFloat());
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
            this.set(Float.parseFloat(value));
        }
        catch (Exception e)
        {}

        return false;
    }

    @Override
    public void copy(Value value)
    {
        if (value instanceof ValueFloat)
        {
            this.value = ((ValueFloat) value).value;
        }
    }

    @Override
    public void copyServer(Value value)
    {
        if (value instanceof ValueFloat)
        {
            this.serverValue = ((ValueFloat) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);

        this.value = buffer.readFloat();
        this.defaultValue = buffer.readFloat();
        this.min = buffer.readFloat();
        this.max = buffer.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);

        buffer.writeFloat(this.value);
        buffer.writeFloat(this.defaultValue);
        buffer.writeFloat(this.min);
        buffer.writeFloat(this.max);
    }

    @Override
    public String toString()
    {
        return Float.toString(this.value);
    }
}