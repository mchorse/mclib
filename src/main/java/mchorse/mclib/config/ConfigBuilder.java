package mchorse.mclib.config;

import mchorse.mclib.config.values.Value;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueDouble;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueRL;
import mchorse.mclib.config.values.ValueString;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigBuilder
{
    public final String id;
    public final File file;

    private List<ConfigCategory> categories = new ArrayList<ConfigCategory>();
    private ConfigCategory category;

    public ConfigBuilder(String id, File file)
    {
        this.id = id;
        this.file = file;
    }

    public ConfigCategory getCategory()
    {
        return this.category;
    }

    public Config build()
    {
        Config config = new Config(this.id, this.file);

        for (ConfigCategory category : this.categories)
        {
            category.config = config;
            config.categories.put(category.id, category);
        }

        return config;
    }

    public ConfigBuilder category(String id)
    {
        this.categories.add(this.category = new ConfigCategory(id));

        return this;
    }

    public ConfigBuilder register(Value value)
    {
        this.category.register(value.getId(), value);

        return this;
    }

    public ValueInt getInt(String id, int defaultValue)
    {
        ValueInt value = new ValueInt(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueInt getInt(String id, int defaultValue, int min, int max)
    {
        ValueInt value = new ValueInt(id, defaultValue, min, max);

        this.register(value);

        return value;
    }

    public ValueFloat getFloat(String id, float defaultValue)
    {
        ValueFloat value = new ValueFloat(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueFloat getFloat(String id, float defaultValue, float min, float max)
    {
        ValueFloat value = new ValueFloat(id, defaultValue, min, max);

        this.register(value);

        return value;
    }

    public ValueDouble getDouble(String id, double defaultValue)
    {
        ValueDouble value = new ValueDouble(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueDouble getDouble(String id, double defaultValue, double min, double max)
    {
        ValueDouble value = new ValueDouble(id, defaultValue, min, max);

        this.register(value);

        return value;
    }

    public ValueBoolean getBoolean(String id, boolean defaultValue)
    {
        ValueBoolean value = new ValueBoolean(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueString getString(String id, String defaultValue)
    {
        ValueString value = new ValueString(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueRL getRL(String id, ResourceLocation defaultValue)
    {
        ValueRL value = new ValueRL(id, defaultValue);

        this.register(value);

        return value;
    }
}