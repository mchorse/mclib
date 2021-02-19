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

public abstract class ValueGUI extends Value
{
    public ValueGUI(String id)
    {
        super(id);
    }

    @Override
    public void reset()
    {}

    @Override
    public void resetServer()
    {}

    @Override
    @SideOnly(Side.CLIENT)
    public abstract List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui);

    @Override
    public boolean parseFromCommand(String value)
    {
        return false;
    }

    @Override
    public void fromJSON(JsonElement element)
    {}

    @Override
    public void copyServer(IConfigValue value)
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