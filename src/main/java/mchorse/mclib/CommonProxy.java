package mchorse.mclib;

import mchorse.mclib.config.ConfigManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class CommonProxy
{
	public ConfigManager configs = new ConfigManager();
	public File configFolder;

	public void preInit(FMLPreInitializationEvent event)
	{
		this.configFolder = event.getModConfigurationDirectory();
	}

	public void init(FMLInitializationEvent event)
	{
		this.configs.register(this.configFolder);
	}
}