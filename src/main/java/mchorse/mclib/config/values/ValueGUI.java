package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.config.gui.GuiConfigPanel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class ValueGUI extends Value implements IConfigGuiProvider
{
    public ValueGUI(String id)
    {
        super(id);
    }

    @Override
    public Object getValue()
    {
        return null;
    }

    @Override
    public void setValue(Object value)
    {}

    @Override
    public void reset()
    {}

    @Override
    public void fromJSON(JsonElement element)
    {}

    @Override
    public void copy(IConfigValue value)
    {}

    @Override
    public JsonElement toJSON()
    {
        return JsonNull.INSTANCE;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {}

    @Override
    public void toBytes(ByteBuf buffer)
    {}
}