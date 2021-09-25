package mchorse.mclib.client.gui.utils;

import mchorse.mclib.McLib;
import net.minecraft.util.ResourceLocation;

public class Icons
{
    /**
     * Icons texture used across all dashboard panels
     */
    public static final ResourceLocation ICONS = new ResourceLocation(McLib.MOD_ID, "textures/gui/icons.png");

    public static final Icon NONE = new Icon(null, 0, 0);

    public static final Icon GEAR = new Icon(ICONS, 0, 0);
    public static final Icon MORE = new Icon(ICONS, 16, 0);
    public static final Icon SAVED = new Icon(ICONS, 32, 0);
    public static final Icon SAVE = new Icon(ICONS, 48, 0);
    public static final Icon ADD = new Icon(ICONS, 64, 0);
    public static final Icon DUPE = new Icon(ICONS, 80, 0);
    public static final Icon REMOVE = new Icon(ICONS, 96, 0);
    public static final Icon POSE = new Icon(ICONS, 112, 0);
    public static final Icon FILTER = new Icon(ICONS, 128, 0);
    public static final Icon MOVE_UP = new Icon(ICONS, 144, 0, 16, 8);
    public static final Icon MOVE_DOWN = new Icon(ICONS, 144, 8, 16, 8);
    public static final Icon LOCKED = new Icon(ICONS, 160, 0);
    public static final Icon UNLOCKED = new Icon(ICONS, 176, 0);
    public static final Icon COPY = new Icon(ICONS, 192, 0);
    public static final Icon PASTE = new Icon(ICONS, 208, 0);
    public static final Icon CUT = new Icon(ICONS, 224, 0);
    public static final Icon REFRESH = new Icon(ICONS, 240, 0);

    public static final Icon DOWNLOAD = new Icon(ICONS, 0, 16);
    public static final Icon UPLOAD = new Icon(ICONS, 16, 16);
    public static final Icon SERVER = new Icon(ICONS, 32, 16);
    public static final Icon FOLDER = new Icon(ICONS, 48, 16);
    public static final Icon IMAGE = new Icon(ICONS, 64, 16);
    public static final Icon EDIT = new Icon(ICONS, 80, 16);
    public static final Icon MATERIAL = new Icon(ICONS, 96, 16);
    public static final Icon CLOSE = new Icon(ICONS, 112, 16);
    public static final Icon LIMB = new Icon(ICONS, 128, 16);
    public static final Icon CODE = new Icon(ICONS, 144, 16);
    public static final Icon MOVE_LEFT = new Icon(ICONS, 144, 16, 8, 16);
    public static final Icon MOVE_RIGHT = new Icon(ICONS, 152, 16, 8, 16);
    public static final Icon HELP = new Icon(ICONS, 160, 16);
    public static final Icon LEFT_HANDLE = new Icon(ICONS, 176, 16);
    public static final Icon MAIN_HANDLE = new Icon(ICONS, 192, 16);
    public static final Icon RIGHT_HANDLE = new Icon(ICONS, 208, 16);
    public static final Icon REVERSE = new Icon(ICONS, 224, 16);
    public static final Icon BLOCK = new Icon(ICONS, 240, 16);

    public static final Icon FAVORITE = new Icon(ICONS, 0, 32);
    public static final Icon VISIBLE = new Icon(ICONS, 16, 32);
    public static final Icon INVISIBLE = new Icon(ICONS, 32, 32);
    public static final Icon PLAY = new Icon(ICONS, 48, 32);
    public static final Icon PAUSE = new Icon(ICONS, 64, 32);
    public static final Icon MAXIMIZE = new Icon(ICONS, 80, 32);
    public static final Icon MINIMIZE = new Icon(ICONS, 96, 32);
    public static final Icon STOP = new Icon(ICONS, 112, 32);
    public static final Icon FULLSCREEN = new Icon(ICONS, 128, 32);
    public static final Icon ALL_DIRECTIONS = new Icon(ICONS, 144, 32);
    public static final Icon SPHERE = new Icon(ICONS, 160, 32);
    public static final Icon SHIFT_TO = new Icon(ICONS, 176, 32);
    public static final Icon SHIFT_FORWARD = new Icon(ICONS, 192, 32);
    public static final Icon SHIFT_BACKWARD = new Icon(ICONS, 208, 32);
    public static final Icon MOVE_TO = new Icon(ICONS, 224, 32);
    public static final Icon GRAPH = new Icon(ICONS, 240, 32);

    public static final Icon WRENCH = new Icon(ICONS, 0, 48);
    public static final Icon EXCLAMATION = new Icon(ICONS, 16, 48);
    public static final Icon LEFTLOAD = new Icon(ICONS, 32, 48);
    public static final Icon RIGHTLOAD = new Icon(ICONS, 48, 48);
    public static final Icon BUBBLE = new Icon(ICONS, 64, 48);
    public static final Icon FILE = new Icon(ICONS, 80, 48);
    public static final Icon PROCESSOR = new Icon(ICONS, 96, 48);
    public static final Icon MAZE = new Icon(ICONS, 112, 48);
    public static final Icon BOOKMARK = new Icon(ICONS, 128, 48);
    public static final Icon SOUND = new Icon(ICONS, 144, 48);
    public static final Icon SEARCH = new Icon(ICONS, 160, 48);

    public static final Icon CHECKBOARD = new Icon(ICONS, 0, 240);
    public static final Icon DISABLED = new Icon(ICONS, 16, 240);
    public static final Icon CURSOR = new Icon(ICONS, 32, 240);

    public static void register()
    {
        IconRegistry.register("gear", GEAR);
        IconRegistry.register("more", MORE);
        IconRegistry.register("saved", SAVED);
        IconRegistry.register("save", SAVE);
        IconRegistry.register("add", ADD);
        IconRegistry.register("dupe", DUPE);
        IconRegistry.register("remove", REMOVE);
        IconRegistry.register("pose", POSE);
        IconRegistry.register("filter", FILTER);
        IconRegistry.register("move_up", MOVE_UP);
        IconRegistry.register("move_down", MOVE_DOWN);
        IconRegistry.register("locked", LOCKED);
        IconRegistry.register("unlocked", UNLOCKED);
        IconRegistry.register("copy", COPY);
        IconRegistry.register("paste", PASTE);
        IconRegistry.register("cut", CUT);
        IconRegistry.register("refresh", REFRESH);
        
        IconRegistry.register("download", DOWNLOAD);
        IconRegistry.register("upload", UPLOAD);
        IconRegistry.register("server", SERVER);
        IconRegistry.register("folder", FOLDER);
        IconRegistry.register("image", IMAGE);
        IconRegistry.register("edit", EDIT);
        IconRegistry.register("material", MATERIAL);
        IconRegistry.register("close", CLOSE);
        IconRegistry.register("limb", LIMB);
        IconRegistry.register("code", CODE);
        IconRegistry.register("move_left", MOVE_LEFT);
        IconRegistry.register("move_right", MOVE_RIGHT);
        IconRegistry.register("help", HELP);
        IconRegistry.register("left_handle", LEFT_HANDLE);
        IconRegistry.register("main_handle", MAIN_HANDLE);
        IconRegistry.register("right_handle", RIGHT_HANDLE);
        IconRegistry.register("reverse", REVERSE);
        IconRegistry.register("block", BLOCK);
        IconRegistry.register("favorite", FAVORITE);
        IconRegistry.register("visible", VISIBLE);
        IconRegistry.register("invisible", INVISIBLE);
        IconRegistry.register("play", PLAY);
        IconRegistry.register("pause", PAUSE);
        IconRegistry.register("maximize", MAXIMIZE);
        IconRegistry.register("minimize", MINIMIZE);
        IconRegistry.register("stop", STOP);
        IconRegistry.register("fullscreen", FULLSCREEN);
        IconRegistry.register("all_directions", ALL_DIRECTIONS);
        IconRegistry.register("sphere", SPHERE);
        IconRegistry.register("shift_to", SHIFT_TO);
        IconRegistry.register("shift_forward", SHIFT_FORWARD);
        IconRegistry.register("shift_backward", SHIFT_BACKWARD);
        IconRegistry.register("move_to", MOVE_TO);
        IconRegistry.register("graph", GRAPH);
        IconRegistry.register("wrench", WRENCH);
        IconRegistry.register("exclamation", EXCLAMATION);
        IconRegistry.register("leftload", LEFTLOAD);
        IconRegistry.register("rightload", RIGHTLOAD);
        IconRegistry.register("bubble", BUBBLE);
        IconRegistry.register("file", FILE);
        IconRegistry.register("processor", PROCESSOR);
        IconRegistry.register("maze", MAZE);
        IconRegistry.register("bookmark", BOOKMARK);
        IconRegistry.register("sound", SOUND);
        IconRegistry.register("search", SEARCH);
        IconRegistry.register("checkboard", CHECKBOARD);
        IconRegistry.register("disabled", DISABLED);
        IconRegistry.register("cursor", CURSOR);
    }
}