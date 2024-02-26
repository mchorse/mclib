package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.utils.Interpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueDouble extends GenericNumberValue<Double> implements IServerValue, IConfigGuiProvider
{
    public ValueDouble(String id)
    {
        super(id, 0D, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public ValueDouble(String id, double defaultValue)
    {
        super(id, defaultValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public ValueDouble(String id, double defaultValue, double min, double max)
    {
        super(id, defaultValue, min, max);
    }

    @Override
    public void resetServer()
    {
        this.serverValue = null;
    }

    @Override
    protected Double getNullValue()
    {
        return 0D;
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
        this.set(element.getAsDouble());
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        try
        {
            this.set(Double.parseDouble(value));
        }
        catch (Exception e)
        {}

        return false;
    }

    @Override
    public void copy(Value value)
    {
        if (value instanceof ValueDouble)
        {
            this.set(((ValueDouble) value).value);
        }
    }

    @Override
    public void copyServer(Value value)
    {
        if (value instanceof ValueDouble)
        {
            this.serverValue = ((ValueDouble) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);

        this.defaultValue = buffer.readDouble();
        this.min = buffer.readDouble();
        this.max = buffer.readDouble();
        this.valueFromBytes(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);

        buffer.writeDouble(this.defaultValue);
        buffer.writeDouble(this.min);
        buffer.writeDouble(this.max);
        this.valueToBytes(buffer);
    }

    @Override
    public void valueFromBytes(ByteBuf buffer)
    {
        this.set(buffer.readDouble());
    }

    @Override
    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeDouble(this.value);
    }

    @Override
    public String toString()
    {
        return Double.toString(this.value);
    }

    @Override
    public void valueFromNBT(NBTBase tag)
    {
        if (tag instanceof NBTPrimitive)
        {
            this.set(((NBTPrimitive) tag).getDouble());
        }
    }

    @Override
    public NBTBase valueToNBT()
    {
        return new NBTTagDouble(this.value);
    }

    @Override
    public ValueDouble copy()
    {
        ValueDouble clone = new ValueDouble(this.id, this.defaultValue, this.min, this.max);
        clone.value = this.value;

        return clone;
    }

    public Double interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Double)) return this.value;

        return interpolation.interpolate(this.value, (Double) to.value, factor);
    }
}