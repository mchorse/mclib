package mchorse.mclib.config;

import com.google.common.base.Predicates;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.config.json.ConfigParser;
import mchorse.mclib.config.values.IConfigValue;
import mchorse.mclib.network.IByteBufSerializable;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketConfig;
import mchorse.mclib.utils.JsonUtils;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Config implements IByteBufSerializable
{
    public final String id;
    public final File file;

    public final Map<String, ConfigCategory> categories = new LinkedHashMap<String, ConfigCategory>();

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
        for (ConfigCategory category : this.categories.values())
        {
            if (category.hasSyncable())
            {
                return true;
            }
        }

        return false;
    }

    /* Translation string related methods */

    @SideOnly(Side.CLIENT)
    public String getTitle()
    {
        return I18n.format(this.getTitleKey());
    }

    @SideOnly(Side.CLIENT)
    public String getTitleKey()
    {
        return this.id + ".config.title";
    }

    @SideOnly(Side.CLIENT)
    public String getCategoryTitle(String category)
    {
        return I18n.format(this.getCategoryTitleKey(category));
    }

    @SideOnly(Side.CLIENT)
    public String getCategoryTitleKey(String category)
    {
        return this.id + ".config." + category + ".title";
    }

    @SideOnly(Side.CLIENT)
    public String getCategoryTooltip(String category)
    {
        return I18n.format(this.getCategoryTooltipKey(category));
    }

    @SideOnly(Side.CLIENT)
    public String getCategoryTooltipKey(String category)
    {
        return this.id + ".config." + category + ".tooltip";
    }

    @SideOnly(Side.CLIENT)
    public String getValueTitle(String category, String value)
    {
        return I18n.format(this.getValueTitleKey(category, value));
    }

    @SideOnly(Side.CLIENT)
    public String getValueTitleKey(String category, String value)
    {
        return this.id + ".config." + category + "." + value;
    }

    @SideOnly(Side.CLIENT)
    public String getValueTooltip(String category, String value)
    {
        return I18n.format(this.getValueTooltipKey(category, value));
    }

    @SideOnly(Side.CLIENT)
    public String getValueTooltipKey(String category, String value)
    {
        return this.id + ".config.comments." + category + "." + value;
    }

    /**
     * Get a value from category by their ids
     */
    public IConfigValue get(String category, String value)
    {
        ConfigCategory cat = this.categories.get(category);

        if (cat != null)
        {
            return cat.values.get(value);
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
            if (this.file != null)
            {
                FileUtils.writeStringToFile(this.file, this.toJSON(), Charset.defaultCharset());
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
        for (Map.Entry<String, ConfigCategory> entry : config.categories.entrySet())
        {
            this.categories.get(entry.getKey()).copy(entry.getValue());
        }
    }

    public void copyServer(Config config)
    {
        for (Map.Entry<String, ConfigCategory> entry : config.categories.entrySet())
        {
            this.categories.get(entry.getKey()).copyServer(entry.getValue());
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
        return this.filter(IConfigValue::isSyncable);
    }

    public Config filterServerSide()
    {
        return this.filter(Predicates.not(IConfigValue::isClientSide));
    }

    public Config filter(Predicate<IConfigValue> predicate)
    {
        Config config = new Config(this.id);

        for (ConfigCategory category : this.categories.values())
        {
            List<IConfigValue> values = new ArrayList<IConfigValue>(category.values.values().stream().filter(predicate).collect(Collectors.toList()));

            if (!values.isEmpty())
            {
                ConfigCategory newCategory = new ConfigCategory(category.id);

                newCategory.config = config;

                for (IConfigValue value : values)
                {
                    newCategory.values.put(value.getId(), value);
                }

                config.categories.put(newCategory.id, newCategory);
            }
        }

        return config;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.categories.clear();

        for (int i = 0, c = buffer.readInt(); i < c; i++)
        {
            String key = ByteBufUtils.readUTF8String(buffer);
            ConfigCategory category = new ConfigCategory(key);

            category.config = this;
            category.fromBytes(buffer);
            this.categories.put(key, category);
        }
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.categories.size());

        for (Map.Entry<String, ConfigCategory> entry : this.categories.entrySet())
        {
            ByteBufUtils.writeUTF8String(buffer, entry.getKey());

            entry.getValue().toBytes(buffer);
        }
    }

    public void resetServerValues()
    {
        for (ConfigCategory category : this.categories.values())
        {
            category.resetServerValues();
        }
    }
}