package mchorse.mclib.config;

import mchorse.mclib.McLib;
import mchorse.mclib.config.json.ConfigParser;
import mchorse.mclib.events.RegisterConfigEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager
{
    public final Map<String, Config> modules = new HashMap<String, Config>();

    public void register(File configs)
    {
        RegisterConfigEvent event = new RegisterConfigEvent(configs);

        McLib.EVENT_BUS.post(event);

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