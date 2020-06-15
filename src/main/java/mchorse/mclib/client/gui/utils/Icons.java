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

	public static final Icon CHECKBOARD = new Icon(ICONS, 0, 240, 16, 16);
}