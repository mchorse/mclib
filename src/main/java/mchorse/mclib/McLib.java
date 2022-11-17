package mchorse.mclib;

import mchorse.mclib.client.gui.utils.ValueColors;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.commands.CommandMcLib;
import mchorse.mclib.commands.CommandCheats;
import mchorse.mclib.commands.utils.L10n;
import mchorse.mclib.events.RegisterPermissionsEvent;
import mchorse.mclib.permissions.McLibPermissions;
import mchorse.mclib.config.ConfigBuilder;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueRL;
import mchorse.mclib.events.RegisterConfigEvent;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.math.Operation;
import mchorse.mclib.math.Operator;
import mchorse.mclib.math.Variable;
import mchorse.mclib.permissions.PermissionCategory;
import mchorse.mclib.permissions.PermissionFactory;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.PayloadASM;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public static final Logger LOGGER = LogManager.getLogger(McLib.MOD_ID);

    public static L10n l10n = new L10n(MOD_ID);

    /**
     * A factory containing all permissions that have been registered through the {@link RegisterPermissionsEvent}.
     */
    public static final PermissionFactory permissionFactory = new PermissionFactory();

    /* Configuration */
    public static ValueBoolean opDropItems;

    public static ValueBoolean debugPanel;
    public static ValueColors favoriteColors;
    public static ValueInt primaryColor;
    public static ValueBoolean enableBorders;
    public static ValueBoolean enableCheckboxRendering;
    public static ValueBoolean enableTrackpadIncrements;
    public static ValueBoolean enableGridRendering;
    public static ValueInt userIntefaceScale;
    public static ValueInt tooltipStyle;
    public static ValueInt trackpadDecimalPlaces;
    public static ValueBoolean renderTranslateTextColors;

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
    public static ValueBoolean multiskinClear;

    public static ValueInt maxPacketSize;

    @SubscribeEvent
    public void onConfigRegister(RegisterConfigEvent event)
    {
        opDropItems = event.opAccess.category(MOD_ID).getBoolean("drop_items", true);

        /* McLib's options */
        ConfigBuilder builder = event.createBuilder(MOD_ID);

        /* Appearance category */
        debugPanel = builder.category("appearance").getBoolean("debug_panel", false);
        debugPanel.invisible();
        primaryColor = builder.getInt("primary_color", 0x0088ff).color();
        enableBorders = builder.getBoolean("enable_borders", false);
        enableCheckboxRendering = builder.getBoolean("enable_checkbox_rendering", false);
        enableTrackpadIncrements = builder.getBoolean("enable_trackpad_increments", true);
        trackpadDecimalPlaces = builder.getInt("trackpad_decimal_places", 6, 3, 31);
        enableGridRendering = builder.getBoolean("enable_grid_rendering", true);
        userIntefaceScale = builder.getInt("user_interface_scale", 2, 0, 4);
        tooltipStyle = builder.getInt("tooltip_style", 1).modes(
            IKey.lang("mclib.tooltip_style.light"),
            IKey.lang("mclib.tooltip_style.dark")
        );
        renderTranslateTextColors = builder.getBoolean("render_translation_text_colours", false);

        favoriteColors = new ValueColors("favorite_colors");
        builder.register(favoriteColors);

        builder.getCategory().markClientSide();

        /* Tutorials category */
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

        builder.getCategory().markClientSide();

        /* Background category */
        backgroundImage = builder.category("background").getRL("image",  null);
        backgroundColor = builder.getInt("color",  0xcc000000).colorAlpha();

        builder.getCategory().markClientSide();

        /* Scrollbars category */
        scrollbarFlat = builder.category("scrollbars").getBoolean("flat", false);
        scrollbarShadow = builder.getInt("shadow", ColorUtils.HALF_BLACK).colorAlpha();
        scrollbarWidth = builder.getInt("width", 4, 2, 10);

        builder.getCategory().markClientSide();

        /* Multiskin category */
        multiskinMultiThreaded = builder.category("multiskin").getBoolean("multithreaded", true);
        multiskinClear = builder.getBoolean("clear", true);

        builder.getCategory().markClientSide();

        /* Vanilla category */
        maxPacketSize = builder.category("vanilla").getInt("max_packet_size", PayloadASM.MIN_SIZE, PayloadASM.MIN_SIZE, Integer.MAX_VALUE / 4);
        maxPacketSize.syncable();
    }

    @SubscribeEvent
    public void onPermissionRegister(RegisterPermissionsEvent event)
    {
        event.registerMod(MOD_ID, DefaultPermissionLevel.OP);

        McLibPermissions.configEdit = event.registerPermission(new PermissionCategory("edit_config"));

        event.registerCategory(new PermissionCategory("gui"));
        McLibPermissions.accessGui = event.registerPermission(new PermissionCategory("access_gui"));

        event.endCategory();
        event.endCategory();
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

    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent event)
    {
        if (event.getServer().isSinglePlayer())
        {
            event.registerServerCommand(new CommandCheats());
        }

        event.registerServerCommand(new CommandMcLib());
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
        test(builder, "1 << 4 - 1", 1 << 4 - 1);
        test(builder, "256 >> 4 + 2", 256 >> 4 + 2);
        test(builder, "255 & 7 + 1", 255 & 7 + 1);
        test(builder, "256 | 7 + 1", 256 | 7 + 1);
        test(builder, "5 % 2 + 1 == 0 * 2", 5 % 2 + 1 == 0 * 2 ? 1 : 0);

        builder.variables.put("abc", new Variable("abc", 1));
        IValue test = builder.parse("- (40 + 2) / -2");

        System.out.println(test.isNumber() + " " + test.stringValue() + " " + test.booleanValue() + " " + test.doubleValue());
    }

    public static void test(MathBuilder builder, String expression, double result) throws Exception
    {
        IValue value = builder.parse(expression);

        System.out.println(expression + " = " + value.get() + " (" + result + ") is " + Operation.equals(value.get().doubleValue(), result));
        System.out.println(value.toString() + "\n");
    }
}