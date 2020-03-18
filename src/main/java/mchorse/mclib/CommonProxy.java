package mchorse.mclib;

import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueString;
import mchorse.mclib.events.RegisterConfigEvent;
import mchorse.mclib.config.ConfigBuilder;
import mchorse.mclib.config.ConfigManager;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.mclib.config.values.ValueInt;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class CommonProxy
{
	public ConfigManager configs = new ConfigManager();
	public File configFolder;

	public void preInit(FMLPreInitializationEvent event)
	{
		this.configFolder = event.getModConfigurationDirectory();

		McLib.EVENT_BUS.register(this);
	}

	public void init(FMLInitializationEvent event)
	{
		this.configs.register(this.configFolder);
	}

	@SubscribeEvent
	public void onConfigRegister(RegisterConfigEvent event)
	{
		ConfigBuilder builder = new ConfigBuilder("mclib", new File(this.configFolder, "mclib/config.json"));

		ValueInt delay = builder.category("general").getInt("delay", 10, 0, 20);
		ValueInt frequency = builder.getInt("frequency", 100);
		ValueFloat value = builder.getFloat("value", 0F, -1F, 1F);

		ValueInt test = builder.category("textures").getInt("test", 0, -10, 10);
		ValueString name = builder.getString("name", "Bob");
		ValueString lastname = builder.getString("lastname", "Cool");
		ValueBoolean enabled = builder.getBoolean("enabled", false);

		ValueString id = builder.category("new").getString("id", "123456");
		ValueBoolean cool = builder.getBoolean("cool", true);

		event.modules.add(builder.build());
	}
}