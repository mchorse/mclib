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

    public static final Icon GEAR = IconRegistry.register("gear", new Icon(ICONS, 0, 0));
    public static final Icon MORE = IconRegistry.register("more", new Icon(ICONS, 16, 0));
    public static final Icon SAVED = IconRegistry.register("saved", new Icon(ICONS, 32, 0));
    public static final Icon SAVE = IconRegistry.register("save", new Icon(ICONS, 48, 0));
    public static final Icon ADD = IconRegistry.register("add", new Icon(ICONS, 64, 0));
    public static final Icon DUPE = IconRegistry.register("dupe", new Icon(ICONS, 80, 0));
    public static final Icon REMOVE = IconRegistry.register("remove", new Icon(ICONS, 96, 0));
    public static final Icon POSE = IconRegistry.register("pose", new Icon(ICONS, 112, 0));
    public static final Icon FILTER = IconRegistry.register("filter", new Icon(ICONS, 128, 0));
    public static final Icon MOVE_UP = IconRegistry.register("move_up", new Icon(ICONS, 144, 0, 16, 8));
    public static final Icon MOVE_DOWN = IconRegistry.register("move_down", new Icon(ICONS, 144, 8, 16, 8));
    public static final Icon LOCKED = IconRegistry.register("locked", new Icon(ICONS, 160, 0));
    public static final Icon UNLOCKED = IconRegistry.register("unlocked", new Icon(ICONS, 176, 0));
    public static final Icon COPY = IconRegistry.register("copy", new Icon(ICONS, 192, 0));
    public static final Icon PASTE = IconRegistry.register("paste", new Icon(ICONS, 208, 0));
    public static final Icon CUT = IconRegistry.register("cut", new Icon(ICONS, 224, 0));
    public static final Icon REFRESH = IconRegistry.register("refresh", new Icon(ICONS, 240, 0));

    public static final Icon DOWNLOAD = IconRegistry.register("download", new Icon(ICONS, 0, 16));
    public static final Icon UPLOAD = IconRegistry.register("upload", new Icon(ICONS, 16, 16));
    public static final Icon SERVER = IconRegistry.register("server", new Icon(ICONS, 32, 16));
    public static final Icon FOLDER = IconRegistry.register("folder", new Icon(ICONS, 48, 16));
    public static final Icon IMAGE = IconRegistry.register("image", new Icon(ICONS, 64, 16));
    public static final Icon EDIT = IconRegistry.register("edit", new Icon(ICONS, 80, 16));
    public static final Icon MATERIAL = IconRegistry.register("material", new Icon(ICONS, 96, 16));
    public static final Icon CLOSE = IconRegistry.register("close", new Icon(ICONS, 112, 16));
    public static final Icon LIMB = IconRegistry.register("limb", new Icon(ICONS, 128, 16));
    public static final Icon CODE = IconRegistry.register("code", new Icon(ICONS, 144, 16));
    public static final Icon MOVE_LEFT = IconRegistry.register("move_left", new Icon(ICONS, 144, 16, 8, 16));
    public static final Icon MOVE_RIGHT = IconRegistry.register("move_right", new Icon(ICONS, 152, 16, 8, 16));
    public static final Icon HELP = IconRegistry.register("help", new Icon(ICONS, 160, 16));
    public static final Icon LEFT_HANDLE = IconRegistry.register("left_handle", new Icon(ICONS, 176, 16));
    public static final Icon MAIN_HANDLE = IconRegistry.register("main_handle", new Icon(ICONS, 192, 16));
    public static final Icon RIGHT_HANDLE = IconRegistry.register("right_handle", new Icon(ICONS, 208, 16));
    public static final Icon REVERSE = IconRegistry.register("reverse", new Icon(ICONS, 224, 16));
    public static final Icon BLOCK = IconRegistry.register("block", new Icon(ICONS, 240, 16));

    public static final Icon FAVORITE = IconRegistry.register("favorite", new Icon(ICONS, 0, 32));
    public static final Icon VISIBLE = IconRegistry.register("visible", new Icon(ICONS, 16, 32));
    public static final Icon INVISIBLE = IconRegistry.register("invisible", new Icon(ICONS, 32, 32));
    public static final Icon PLAY = IconRegistry.register("play", new Icon(ICONS, 48, 32));
    public static final Icon PAUSE = IconRegistry.register("pause", new Icon(ICONS, 64, 32));
    public static final Icon MAXIMIZE = IconRegistry.register("maximize", new Icon(ICONS, 80, 32));
    public static final Icon MINIMIZE = IconRegistry.register("minimize", new Icon(ICONS, 96, 32));
    public static final Icon STOP = IconRegistry.register("stop", new Icon(ICONS, 112, 32));
    public static final Icon FULLSCREEN = IconRegistry.register("fullscreen", new Icon(ICONS, 128, 32));
    public static final Icon ALL_DIRECTIONS = IconRegistry.register("all_directions", new Icon(ICONS, 144, 32));
    public static final Icon SPHERE = IconRegistry.register("sphere", new Icon(ICONS, 160, 32));
    public static final Icon SHIFT_TO = IconRegistry.register("shift_to", new Icon(ICONS, 176, 32));
    public static final Icon SHIFT_FORWARD = IconRegistry.register("shift_forward", new Icon(ICONS, 192, 32));
    public static final Icon SHIFT_BACKWARD = IconRegistry.register("shift_backward", new Icon(ICONS, 208, 32));
    public static final Icon MOVE_TO = IconRegistry.register("move_to", new Icon(ICONS, 224, 32));
    public static final Icon GRAPH = IconRegistry.register("graph", new Icon(ICONS, 240, 32));

    public static final Icon WRENCH = IconRegistry.register("wrench", new Icon(ICONS, 0, 48));
    public static final Icon EXCLAMATION = IconRegistry.register("exclamation", new Icon(ICONS, 16, 48));
    public static final Icon LEFTLOAD = IconRegistry.register("leftload", new Icon(ICONS, 32, 48));
    public static final Icon RIGHTLOAD = IconRegistry.register("rightload", new Icon(ICONS, 48, 48));
    public static final Icon BUBBLE = IconRegistry.register("bubble", new Icon(ICONS, 64, 48));
    public static final Icon FILE = IconRegistry.register("file", new Icon(ICONS, 80, 48));
    public static final Icon PROCESSOR = IconRegistry.register("processor", new Icon(ICONS, 96, 48));
    public static final Icon MAZE = IconRegistry.register("maze", new Icon(ICONS, 112, 48));
    public static final Icon BOOKMARK = IconRegistry.register("bookmark", new Icon(ICONS, 128, 48));
    public static final Icon SOUND = IconRegistry.register("sound", new Icon(ICONS, 144, 48));
    public static final Icon SEARCH = IconRegistry.register("search", new Icon(ICONS, 160, 48));

    public static final Icon CHECKBOARD = IconRegistry.register("checkboard", new Icon(ICONS, 0, 240));
    public static final Icon DISABLED = IconRegistry.register("disabled", new Icon(ICONS, 16, 240));
    public static final Icon CURSOR = IconRegistry.register("cursor", new Icon(ICONS, 32, 240));
}