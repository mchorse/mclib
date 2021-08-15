package mchorse.mclib.utils;

import net.minecraft.client.Minecraft;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.ImmutableList;

public class Keys
{
    public static final String[] KEYS = new String[Keyboard.KEYBOARD_SIZE];
    public static final List<Integer> MODIFIERS = ImmutableList.<Integer>of(Keyboard.KEY_LCONTROL, Keyboard.KEY_LSHIFT, Keyboard.KEY_LMENU, Keyboard.KEY_RCONTROL, Keyboard.KEY_RSHIFT, Keyboard.KEY_RMENU);
    public static final String[] MODNAME = new String[] {"Ctrl", "Shift", "Alt"};

    public static String getKeyName(int key)
    {
        if (key < Keyboard.KEY_NONE || key >= Keyboard.KEYBOARD_SIZE)
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

    /* Combo keys */

    public static int getComboKeyCode(int[] held, int keyCode)
    {
        int comboKey = keyCode;
        int modifierIndex = MODIFIERS.indexOf(keyCode) % 3;

        if (held != null)
        {
            for (int heldKey : held)
            {
                int index = MODIFIERS.indexOf(heldKey) % 3;

                if (index >= 0 && index != modifierIndex)
                {
                    comboKey |= 1 << 31 - index;
                }
            }
        }

        return comboKey;
    }

    public static void main(String...args)
    {
        System.out.println(getComboKeyName(getComboKeyCode(new int[] {Keyboard.KEY_RSHIFT, Keyboard.KEY_LMENU, Keyboard.KEY_LCONTROL, Keyboard.KEY_RCONTROL}, Keyboard.KEY_RMENU)));
    }

    public static int getMainKey(int comboKey)
    {
        int key = comboKey & 0x1FFFFFFF;

        if (key >= Keyboard.KEYBOARD_SIZE)
        {
            key = Keyboard.KEY_NONE;
        }

        return key;
    }

    public static String getComboKeyName(int comboKey)
    {
        StringBuilder builder = new StringBuilder();
        int mainKey = getMainKey(comboKey);

        if (mainKey == Keyboard.KEY_NONE)
        {
            return getKeyName(mainKey);
        }

        for (int i = 0; i < 3; i++)
        {
            if ((comboKey & 1 << 31 - i) != 0)
            {
                builder.append(MODNAME[i]).append(" + ");
            }
        }

        builder.append(getKeyName(mainKey));

        return builder.toString();
    }

    public static boolean checkModifierKeys(int comboKey)
    {
        int index = MODIFIERS.indexOf(getMainKey(comboKey)) % 3;

        for (int i = 0; i < 3; i++)
        {
            if (i == index)
            {
                continue;
            }

            if ((comboKey & 1 << 31 - i) != 0 != isKeyDown(MODIFIERS.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean isKeyDown(int key)
    {
        if (key == Keyboard.KEY_LSHIFT || key == Keyboard.KEY_RSHIFT)
        {
            return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        }
        else if (key == Keyboard.KEY_LCONTROL || key == Keyboard.KEY_RCONTROL)
        {
            return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        }
        else if (key == Keyboard.KEY_LMENU || key == Keyboard.KEY_RMENU)
        {
            return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
        }

        return Keyboard.isKeyDown(key);
    }
}