package mchorse.mclib.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.utils.ValueColors;
import mchorse.mclib.config.json.ConfigParser;
import mchorse.mclib.config.values.IConfigValue;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueDouble;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueRL;
import mchorse.mclib.config.values.ValueString;
import mchorse.mclib.events.RegisterConfigEvent;
import mchorse.mclib.network.IByteBufSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager
{
    public static final BiMap<String, Class<? extends IConfigValue>> TYPES = HashBiMap.<String, Class<? extends  IConfigValue>>create();

    public final Map<String, Config> modules = new HashMap<String, Config>();

    static
    {
        TYPES.put("boolean", ValueBoolean.class);
        TYPES.put("double", ValueDouble.class);
        TYPES.put("float", ValueFloat.class);
        TYPES.put("int", ValueInt.class);
        TYPES.put("rl", ValueRL.class);
        TYPES.put("string", ValueString.class);
        TYPES.put("colors", ValueColors.class);
    }

    public static IConfigValue fromBytes(ByteBuf buffer)
    {
        String key = ByteBufUtils.readUTF8String(buffer);
        String type = ByteBufUtils.readUTF8String(buffer);

        if (type.isEmpty())
        {
            return null;
        }

        try
        {
            Class<? extends IConfigValue> clazz = TYPES.get(type);
            IConfigValue value = clazz.getConstructor(String.class).newInstance(key);

            value.fromBytes(buffer);

            return value;
        }
        catch (Exception e)
        {}

        return null;
    }

    public static void toBytes(ByteBuf buffer, IConfigValue value)
    {
        String type = TYPES.inverse().get(value.getClass());

        ByteBufUtils.writeUTF8String(buffer, value.getId());
        ByteBufUtils.writeUTF8String(buffer, type == null ? "" : type);

        if (type != null)
        {
            value.toBytes(buffer);
        }
    }

    public void register(File configs)
    {
        RegisterConfigEvent event = new RegisterConfigEvent(configs);

        McLib.EVENT_BUS.post(event);

        Config opAccess = event.opAccess.build().serverSide();

        this.modules.put(opAccess.id, opAccess);

        for (Config config : event.modules)
        {
            this.modules.put(config.id, config);
        }

        this.reload();
    }

    public void reload()
    {
        for (Config config : this.modules.values())
        {
            ConfigParser.fromJson(config, config.file);
        }
    }
}