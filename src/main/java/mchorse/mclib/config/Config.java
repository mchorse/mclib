package mchorse.mclib.config;

import com.google.common.base.Predicates;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.config.json.ConfigParser;
import mchorse.mclib.config.values.Value;
import mchorse.mclib.network.IByteBufSerializable;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketConfig;
import mchorse.mclib.utils.JsonUtils;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Config implements IByteBufSerializable
{
    public final String id;
    public final File file;

    public final Map<String, Value> values = new LinkedHashMap<String, Value>();

    private boolean serverSide;

    public Config(String id, File file)
    {
        this.id = id;
        this.file = file;
    }

    public Config(String id)
    {
        this.id = id;
        this.file = null;
    }

    public Config serverSide()
    {
        this.serverSide = true;

        return this;
    }

    public boolean isServerSide()
    {
        return this.serverSide;
    }

    public boolean hasSyncable()
    {
        for (Value value : this.values.values())
        {
            if (value.hasSyncable())
            {
                return true;
            }
        }

        return false;
    }

    /* Translation string related methods */

    @SideOnly(Side.CLIENT)
    public String getTitleKey()
    {
        return this.id + ".config.title";
    }

    @SideOnly(Side.CLIENT)
    public String getCategoryTitleKey(Value value)
    {
        return this.id + ".config." + value.getPath() + ".title";
    }

    @SideOnly(Side.CLIENT)
    public String getCategoryTooltipKey(Value value)
    {
        return this.id + ".config." + value.getPath() + ".tooltip";
    }

    @SideOnly(Side.CLIENT)
    public String getValueLabelKey(Value value)
    {
        return this.id + ".config." + value.getPath();
    }

    @SideOnly(Side.CLIENT)
    public String getValueCommentKey(Value value)
    {
        return this.id + ".config.comments." + value.getPath();
    }

    /**
     * Get a value from category by their ids
     */
    public Value get(String category, String value)
    {
        Value cat = this.values.get(category);

        if (cat != null)
        {
            return cat.getSubValue(value);
        }

        return null;
    }

    /**
     * Save later in a separate thread
     */
    public void saveLater()
    {
        ConfigThread.add(this);
    }

    /**
     * Save config to default location
     */
    public void save()
    {
        this.save(this.file);
    }

    /**
     * Save config to given file
     */
    public boolean save(File file)
    {
        try
        {
            if (file != null)
            {
                FileUtils.writeStringToFile(file, this.toJSON(), Charset.defaultCharset());
            }
            else
            {
                /* If file is null, that means that it was sent from server side */
                Dispatcher.sendToServer(new PacketConfig(this));
            }

            return true;
        }
        catch (IOException e)
        {}

        return false;
    }

    /**
     * Copy all values from given config to this config
     */
    public void copy(Config config)
    {
        for (Map.Entry<String, Value> entry : config.values.entrySet())
        {
            this.values.get(entry.getKey()).copy(entry.getValue());
        }
    }

    public void copyServer(Config config)
    {
        for (Map.Entry<String, Value> entry : config.values.entrySet())
        {
            this.values.get(entry.getKey()).copyServer(entry.getValue());
        }
    }

    /**
     * Convert this config into JSON string
     */
    public String toJSON()
    {
        return JsonUtils.jsonToPretty(ConfigParser.toJson(this));
    }

    public Config filterSyncable()
    {
        return this.filter(Value::isSyncable);
    }

    public Config filterServerSide()
    {
        return this.filter(Predicates.not(Value::isClientSide));
    }

    public Config filter(Predicate<Value> predicate)
    {
        Config config = new Config(this.id);

        for (Value category : this.values.values())
        {
            List<Value> values = category.getSubValues().stream().filter(predicate).collect(Collectors.toList());

            if (!values.isEmpty())
            {
                Value newCategory = new Value(category.id);

                newCategory.setConfig(config);

                for (Value value : values)
                {
                    newCategory.addSubValue(value);
                }

                config.values.put(newCategory.id, newCategory);
            }
        }

        return config;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.values.clear();

        for (int i = 0, c = buffer.readInt(); i < c; i++)
        {
            String key = ByteBufUtils.readUTF8String(buffer);
            Value category = new Value(key);

            category.setConfig(this);
            category.fromBytes(buffer);
            this.values.put(key, category);
        }
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.values.size());

        for (Map.Entry<String, Value> entry : this.values.entrySet())
        {
            ByteBufUtils.writeUTF8String(buffer, entry.getKey());

            entry.getValue().toBytes(buffer);
        }
    }

    public void resetServerValues()
    {
        for (Value category : this.values.values())
        {
            category.resetServerValues();
        }
    }
}