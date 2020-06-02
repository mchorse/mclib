package mchorse.mclib;

import mchorse.mclib.config.ConfigBuilder;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueRL;
import mchorse.mclib.events.RegisterConfigEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

/**
 * McLib mod
 * 
 * All it does is provides common code for McHorse's mods.
 */
@Mod.EventBusSubscriber
@Mod(modid = McLib.MOD_ID, name = "McLib", version = McLib.VERSION, updateJSON = "https://raw.githubusercontent.com/mchorse/mclib/master/version.json")
public class McLib
{
    public static final String MOD_ID = "mclib";
    public static final String VERSION = "%VERSION%";

    /* Proxies */
    public static final String CLIENT_PROXY = "mchorse.mclib.ClientProxy";
    public static final String SERVER_PROXY = "mchorse.mclib.CommonProxy";

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    public static CommonProxy proxy;

    public static final EventBus EVENT_BUS = new EventBus();

    /* Configuration */
    public static ValueInt primaryColor;
    public static ValueBoolean enableBorders;
    public static ValueBoolean enableMouseRendering;
    public static ValueBoolean enableCheckboxRendering;
    public static ValueBoolean enableGridRendering;
    public static ValueInt userIntefaceScale;

    public static ValueRL backgroundImage;
    public static ValueInt backgroundColor;

    @SubscribeEvent
    public void onConfigRegister(RegisterConfigEvent event)
    {
        ConfigBuilder builder = event.createBuilder(MOD_ID);

        primaryColor = builder.category("appearance").getInt("primary_color", 0x0088ff).color();
        enableBorders = builder.getBoolean("enable_borders", false);
        enableMouseRendering = builder.getBoolean("enable_mouse_rendering", false);
        enableCheckboxRendering = builder.getBoolean("enable_checkbox_rendering", false);
        enableGridRendering = builder.getBoolean("enable_grid_rendering", true);
        userIntefaceScale = builder.getInt("user_interface_scale", 2, 0, 4);

        backgroundImage = builder.category("background").getRL("image",  null);
        backgroundColor = builder.getInt("color",  0xcc000000).colorAlpha();

        event.modules.add(builder.build());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);

        EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @NetworkCheckHandler
    public boolean checkModDependencies(Map<String, String> map, Side side)
    {
        return true;
    }
}