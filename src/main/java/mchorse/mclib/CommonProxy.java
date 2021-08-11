package mchorse.mclib;

import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.config.ConfigHandler;
import mchorse.mclib.config.ConfigManager;
import mchorse.mclib.network.mclib.Dispatcher;
import net.minecraftforge.common.MinecraftForge;
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

        Dispatcher.register();

        MinecraftForge.EVENT_BUS.register(new ConfigHandler());
    }

    public void init(FMLInitializationEvent event)
    {
        this.configs.register(this.configFolder);

        Icons.register();
    }
}