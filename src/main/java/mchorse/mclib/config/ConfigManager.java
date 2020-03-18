package mchorse.mclib.config;

import mchorse.mclib.McLib;
import mchorse.mclib.events.RegisterConfigEvent;
import mchorse.mclib.config.json.ConfigParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager
{
	public final List<Config> modules = new ArrayList<Config>();

	public void register(File configs)
	{
		RegisterConfigEvent event = new RegisterConfigEvent(configs);

		McLib.EVENT_BUS.post(event);
		this.modules.addAll(event.modules);
		this.reload();
	}

	public void reload()
	{
		for (Config config : this.modules)
		{
			ConfigParser.fromJson(config, config.file);
		}
	}
}