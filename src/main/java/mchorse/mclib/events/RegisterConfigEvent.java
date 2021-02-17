package mchorse.mclib.events;

import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigBuilder;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegisterConfigEvent extends Event
{
    public final File configs;
    public List<Config> modules = new ArrayList<Config>();

    public final ConfigBuilder opAccess;

    public RegisterConfigEvent(File configs)
    {
        this.configs = configs;

        this.opAccess = this.createBuilder("op_access", "mclib/op_access.json");
    }

    public ConfigBuilder createBuilder(String id)
    {
        return this.createBuilder(id, id + "/config.json");
    }

    public ConfigBuilder createBuilder(String id, String path)
    {
        return new ConfigBuilder(id, new File(this.configs, path));
    }
}