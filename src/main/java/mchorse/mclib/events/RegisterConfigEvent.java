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

	public RegisterConfigEvent(File configs)
	{
		this.configs = configs;
	}

	public ConfigBuilder createBuilder(String id)
	{
		return new ConfigBuilder(id, new File(this.configs, id + "/config.json"));
	}
}