package mchorse.mclib.client.gui.utils;

import java.util.HashMap;
import java.util.Map;

public class IconRegistry
{
    public static final Map<String, Icon> icons = new HashMap<String, Icon>();

    public static Icon register(String key, Icon icon)
    {
        if (icons.containsKey(key))
        {
            try
            {
                throw new IllegalStateException("[Icons] Icon " + key + " was already registered prior...");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            icons.put(key, icon);
        }

        return icon;
    }
}