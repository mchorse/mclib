package mchorse.mclib.utils;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class Keys
{
	public static final String[] KEYS = new String[Keyboard.KEYBOARD_SIZE];

	public static String getKeyName(int key)
	{
		if (key < 0 || key >= 256)
		{
			return null;
		}

		if (KEYS[key] == null)
		{
			KEYS[key] = getKey(key);
		}

		return KEYS[key];
	}

	private static String getKey(int key)
	{
		switch (key)
		{
			case Keyboard.KEY_MINUS:
				return "-";
			case Keyboard.KEY_EQUALS:
				return "=";
			case Keyboard.KEY_LBRACKET:
				return "[";
			case Keyboard.KEY_RBRACKET:
				return "]";
			case Keyboard.KEY_SEMICOLON:
				return ";";
			case Keyboard.KEY_APOSTROPHE:
				return "'";
			case Keyboard.KEY_BACKSLASH:
				return "\\";
			case Keyboard.KEY_COMMA:
				return ",";
			case Keyboard.KEY_PERIOD:
				return ".";
			case Keyboard.KEY_SLASH:
				return "/";
			case Keyboard.KEY_GRAVE:
				return "`";
			case Keyboard.KEY_TAB:
				return "Tab";
			case Keyboard.KEY_CAPITAL:
				return "Caps Lock";
			case Keyboard.KEY_LSHIFT:
				return "L. Shift";
			case Keyboard.KEY_LCONTROL:
				return "L. Ctrl";
			case Keyboard.KEY_LMENU:
				return "L. Alt";
			case Keyboard.KEY_LMETA:
				return Minecraft.IS_RUNNING_ON_MAC ? "L. Cmd" : "L. Win";
			case Keyboard.KEY_RSHIFT:
				return "R. Shift";
			case Keyboard.KEY_RCONTROL:
				return "R. Ctrl";
			case Keyboard.KEY_RMENU:
				return "R. Alt";
			case Keyboard.KEY_RMETA:
				return Minecraft.IS_RUNNING_ON_MAC ? "R. Cmd" : "R. Win";
			case Keyboard.KEY_DIVIDE:
				return "Numpad /";
			case Keyboard.KEY_MULTIPLY:
				return "Numpad *";
			case Keyboard.KEY_SUBTRACT:
				return "Numpad -";
			case Keyboard.KEY_ADD:
				return "Numpad +";
			case Keyboard.KEY_DECIMAL:
				return "Numpad .";
		}

		String name = Keyboard.getKeyName(key);

		if (name.length() > 1)
		{
			name = name.substring(0, 1) + name.substring(1).toLowerCase();
		}

		if (name.startsWith("Numpad"))
		{
			name = name.replace("Numpad", "Numpad ");
		}

		return name;
	}
}