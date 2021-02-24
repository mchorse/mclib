package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.config.gui.GuiConfigPanel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueBoolean extends Value implements IServerValue
{
    private boolean value;
    private boolean defaultValue;

    private Boolean serverValue;

    public ValueBoolean(String id)
    {
        super(id);
    }

    public ValueBoolean(String id, boolean defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;

        this.reset();
    }

    public boolean get()
    {
        return this.serverValue == null ? this.value : this.serverValue;
    }

    public void set(boolean value)
    {
        this.value = value;
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
        if (value instanceof Boolean)
        {
            this.set((Boolean) value);
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
        GuiToggleElement toggle = new GuiToggleElement(mc, this);

        toggle.flex().reset();

        return Arrays.asList(toggle);
    }

    @Override
    public void fromJSON(JsonElement element)
    {
        this.set(element.getAsBoolean());
    }

    @Override
    public JsonElement toJSON()
    {
        return new JsonPrimitive(this.value);
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        if (value.equals("1"))
        {
            this.set(true);
        }
        else if (value.equals("0"))
        {
            this.set(false);
        }
        else
        {
            this.set(Boolean.parseBoolean(value));
        }

        return true;
    }

    @Override
    public void copy(IConfigValue value)
    {
        if (value instanceof ValueBoolean)
        {
            this.value = ((ValueBoolean) value).value;
        }
    }

    @Override
    public void copyServer(IConfigValue value)
    {
        if (value instanceof ValueBoolean)
        {
            this.serverValue = ((ValueBoolean) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);

        this.value = buffer.readBoolean();
        this.defaultValue = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);

        buffer.writeBoolean(this.value);
        buffer.writeBoolean(this.defaultValue);
    }

    @Override
    public String toString()
    {
        return Boolean.toString(this.value);
    }
}
