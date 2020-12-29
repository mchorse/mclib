package mchorse.mclib;

import mchorse.mclib.client.gui.utils.ValueColors;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.ConfigBuilder;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueRL;
import mchorse.mclib.events.RegisterConfigEvent;
import mchorse.mclib.events.RenderOverlayEvent;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.math.Operation;
import mchorse.mclib.math.Operator;
import mchorse.mclib.utils.PayloadASM;
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
@Mod(modid = McLib.MOD_ID, name = "McLib", version = McLib.VERSION, updateJSON = "https://raw.githubusercontent.com/mchorse/mclib/1.12/version.json")
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
    public static ValueBoolean debugPanel;
    public static ValueColors favoriteColors;
    public static ValueInt primaryColor;
    public static ValueBoolean enableBorders;
    public static ValueBoolean enableCheckboxRendering;
    public static ValueBoolean enableTrackpadIncrements;
    public static ValueBoolean enableGridRendering;
    public static ValueInt userIntefaceScale;

    public static ValueBoolean enableCursorRendering;
    public static ValueBoolean enableMouseButtonRendering;
    public static ValueBoolean enableKeystrokeRendering;
    public static ValueInt keystrokeOffset;
    public static ValueInt keystrokeMode;

    public static ValueRL backgroundImage;
    public static ValueInt backgroundColor;

    public static ValueBoolean scrollbarFlat;
    public static ValueInt scrollbarShadow;
    public static ValueInt scrollbarWidth;

    public static ValueBoolean multiskinMultiThreaded;

    public static ValueInt maxPacketSize;

    @SubscribeEvent
    public void onConfigRegister(RegisterConfigEvent event)
    {
        ConfigBuilder builder = event.createBuilder(MOD_ID);

        debugPanel = builder.category("appearance").getBoolean("debug_panel", false);
        primaryColor = builder.getInt("primary_color", 0x0088ff).color();
        debugPanel.invisible();
        enableBorders = builder.getBoolean("enable_borders", false);
        enableCheckboxRendering = builder.getBoolean("enable_checkbox_rendering", false);
        enableTrackpadIncrements = builder.getBoolean("enable_trackpad_increments", true);
        enableGridRendering = builder.getBoolean("enable_grid_rendering", true);
        userIntefaceScale = builder.getInt("user_interface_scale", 2, 0, 4);

        favoriteColors = new ValueColors("favorite_colors");
        favoriteColors.invisible();
        builder.register(favoriteColors);

        enableCursorRendering = builder.category("tutorials").getBoolean("enable_mouse_rendering", false);
        enableMouseButtonRendering = builder.getBoolean("enable_mouse_buttons_rendering", false);
        enableKeystrokeRendering = builder.getBoolean("enable_keystrokes_rendering", false);
        keystrokeOffset = builder.getInt("keystroke_offset", 10, 0, 20);
        keystrokeMode = builder.getInt("keystroke_position", 1).modes(
            IKey.lang("mclib.keystrokes_position.auto"),
            IKey.lang("mclib.keystrokes_position.bottom_left"),
            IKey.lang("mclib.keystrokes_position.bottom_right"),
            IKey.lang("mclib.keystrokes_position.top_right"),
            IKey.lang("mclib.keystrokes_position.top_left")
        );

        backgroundImage = builder.category("background").getRL("image",  null);
        backgroundColor = builder.getInt("color",  0xcc000000).colorAlpha();

        scrollbarFlat = builder.category("scrollbars").getBoolean("flat", false);
        scrollbarShadow = builder.getInt("shadow", 0x88000000).colorAlpha();
        scrollbarWidth = builder.getInt("width", 4, 2, 10);

        multiskinMultiThreaded = builder.category("multiskin").getBoolean("multithreaded", true);

        maxPacketSize = builder.category("vanilla").getInt("max_packet_size", PayloadASM.MIN_SIZE, PayloadASM.MIN_SIZE, Integer.MAX_VALUE);

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

    public static void main(String[] args) throws Exception
    {
        Operator.DEBUG = true;
        MathBuilder builder = new MathBuilder();

        test(builder, "1 - 2 * 3 + 4 ", 1 - 2 * 3 + 4  );
        test(builder, "2 * 3 - 8 + 7 ", 2 * 3 - 8 + 7  );
        test(builder, "3 - 7 + 2 * 4 ", 3 - 7 + 2 * 4  );
        test(builder, "8 / 4 - 3 * 10", 8 / 4 - 3 * 10 );
        test(builder, "2 - 4 * 5 / 8 ", 2 - 4 * 5 / 8D );
        test(builder, "3 / 4 * 8 - 10", 3 / 4D * 8 - 10);
        test(builder, "2 * 3 / 4 * 5 ", 2D * 3 / 4 * 5 );
        test(builder, "2 + 3 - 4 + 5 ", 2 + 3 - 4 + 5  );
        test(builder, "7 - 2 ^ 4 - 4 * 5 + 15 ^ 2", 7 - Math.pow(2, 4) - 4 * 5 + Math.pow(15, 2));
        test(builder, "5 -(10 + 20)", 5 -(10 + 20));
    }

    public static void test(MathBuilder builder, String expression, double result) throws Exception
    {
        IValue value = builder.parse(expression);

        System.out.println(expression + " = " + value.get() + " (" + result + ") is " + Operation.equals(value.get(), result));
        System.out.println(value.toString() + "\n");
    }
}