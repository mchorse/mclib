package mchorse.mclib.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.McLib;
import mchorse.mclib.config.values.IConfigValue;
import mchorse.mclib.config.values.Value;
import mchorse.mclib.network.IByteBufSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigCategory implements IByteBufSerializable
{
    public final String id;
    public Config config;
    public final Map<String, IConfigValue> values = new LinkedHashMap<String, IConfigValue>();

    public ConfigCategory(String id)
    {
        this.id = id;
    }

    @SideOnly(Side.CLIENT)
    public String getTitle()
    {
        return this.config.getCategoryTitle(this.id);
    }

    @SideOnly(Side.CLIENT)
    public String getTitleKey()
    {
        return this.config.getCategoryTitleKey(this.id);
    }

    @SideOnly(Side.CLIENT)
    public String getTooltip()
    {
        return this.config.getCategoryTooltip(this.id);
    }

    @SideOnly(Side.CLIENT)
    public String getTooltipKey()
    {
        return this.config.getCategoryTooltipKey(this.id);
    }

    public void register(String id, IConfigValue value)
    {
        if (value instanceof Value)
        {
            ((Value) value).category = this;
        }

        this.values.put(id, value);
    }

    public boolean isVisible()
    {
        for (IConfigValue value : this.values.values())
        {
            if (value.isVisible())
            {
                return true;
            }
        }

        return false;
    }

    public boolean isClientSide()
    {
        for (IConfigValue value : this.values.values())
        {
            if (!value.isClientSide())
            {
                return false;
            }
        }

        return true;
    }

    public void markClientSide()
    {
        for (IConfigValue value : this.values.values())
        {
            if (value instanceof Value)
            {
                ((Value) value).clientSide();
            }
        }
    }

    public boolean hasSyncable()
    {
        for (IConfigValue value : this.values.values())
        {
            if (value.isSyncable())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Copy all values from given config to this config
     */
    public void copy(ConfigCategory category)
    {
        for (Map.Entry<String, IConfigValue> entry : category.values.entrySet())
        {
            this.values.get(entry.getKey()).copy(entry.getValue());
        }
    }

    public void copyServer(ConfigCategory category)
    {
        for (Map.Entry<String, IConfigValue> entry : category.values.entrySet())
        {
            IConfigValue value = this.values.get(entry.getKey());

            if (value != null && value.isSyncable())
            {
                value.copyServer(entry.getValue());
            }
        }
    }

    public JsonObject toJSON()
    {
        JsonObject object = new JsonObject();

        for (IConfigValue value : this.values.values())
        {
            object.add(value.getId(), value.toJSON());
        }

        return object;
    }

    public void fromJSON(JsonObject object)
    {
        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            IConfigValue value = this.values.get(entry.getKey());

            if (value != null)
            {
                value.reset();
                value.fromJSON(entry.getValue());
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.values.clear();

        for (int i = 0, c = buffer.readInt(); i < c; i++)
        {
            IConfigValue value = ConfigManager.fromBytes(buffer);

            if (value != null)
            {
                this.register(value.getId(), value);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.values.size());

        for (Map.Entry<String, IConfigValue> entry : this.values.entrySet())
        {
            ConfigManager.toBytes(buffer, entry.getValue());
        }
    }

    public void resetServerValues()
    {
        for (IConfigValue value : this.values.values())
        {
            value.resetServer();
        }
    }
}