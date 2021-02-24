package mchorse.mclib.client.gui.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.config.values.IConfigValue;
import mchorse.mclib.config.values.IServerValue;
import mchorse.mclib.config.values.Value;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ValueColors extends Value implements IServerValue
{
    private List<Color> colors = new ArrayList<Color>();
    private List<Color> serverColors;

    public ValueColors(String id)
    {
        super(id);
    }

    @Override
    public Object getValue()
    {
        return this.getColors();
    }

    @Override
    public void setValue(Object value)
    {
        if (value instanceof List)
        {
            List list = (List) value;

            if (list.isEmpty())
            {
                return;
            }

            this.colors.clear();

            for (Object object : list)
            {
                if (object instanceof Color)
                {
                    this.colors.add((Color) object);
                }
            }
        }
    }

    public List<Color> getCurrentColors()
    {
        return this.colors;
    }

    public List<Color> getColors()
    {
        return this.serverColors == null ? this.colors : this.serverColors;
    }

    @Override
    public void reset()
    {
        this.colors.clear();
    }

    @Override
    public void resetServer()
    {
        this.serverColors = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        return null;
    }

    @Override
    public void fromJSON(JsonElement element)
    {
        if (!element.isJsonArray())
        {
            return;
        }

        JsonArray array = element.getAsJsonArray();

        for (JsonElement color : array)
        {
            if (color.isJsonPrimitive())
            {
                this.colors.add(new Color().set(color.getAsInt(), true));
            }
        }
    }

    @Override
    public JsonElement toJSON()
    {
        JsonArray array = new JsonArray();

        for (Color color : this.colors)
        {
            array.add(new JsonPrimitive(color.getRGBAColor()));
        }

        return array;
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        String[] splits = value.split(",");
        List<Color> colors = new ArrayList<Color>();

        for (String split : splits)
        {
            try
            {
                int color = ColorUtils.parseColorWithException(split.trim());

                colors.add(new Color().set(color, true));
            }
            catch (Exception e)
            {
                return false;
            }
        }

        this.colors.clear();
        this.colors.addAll(colors);
        this.saveLater();

        return true;
    }

    @Override
    public void copy(IConfigValue value)
    {
        if (value instanceof ValueColors)
        {
            this.colors.clear();
            this.colors.addAll(((ValueColors) value).colors);
        }
    }

    @Override
    public void copyServer(IConfigValue value)
    {
        if (value instanceof ValueColors)
        {
            this.serverColors = ((ValueColors) value).colors;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);

        this.colors.clear();

        for (int i = 0, c = buffer.readInt(); i < c; i++)
        {
            this.colors.add(new Color().set(buffer.readInt(), true));
        }
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);

        buffer.writeInt(this.colors.size());

        for (Color color : this.colors)
        {
            buffer.writeInt(color.getRGBAColor());
        }
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(", ");

        for (Color color : this.colors)
        {
            joiner.add("#" + Integer.toHexString(color.getRGBAColor()));
        }

        return joiner.toString();
    }
}