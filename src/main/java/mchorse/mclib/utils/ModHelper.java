package mchorse.mclib.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class ModHelper
{
    public static ModContainer getCallerMod()
    {
        Class<?> caller = getCallerClass(2);

        if (caller == null)
        {
            return null;
        }

        String jar = caller.getProtectionDomain().getCodeSource().getLocation().getPath();

        if (jar.endsWith(".class"))
        {
            jar = jar.substring(0, jar.lastIndexOf("/bin/") + 4);
        }
        else
        {
            jar = jar.substring(5, jar.lastIndexOf('!'));
        }

        try
        {
            jar = URLDecoder.decode(jar, StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e)
        {}

        File jarFile = new File(jar);

        for (ModContainer mod : Loader.instance().getActiveModList())
        {
            if (jarFile.equals(mod.getSource()))
            {
                return mod;
            }
        }

        return null;
    }

    public static Class<?> getCallerClass(int offset)
    {
        StackTraceElement element = new Exception().getStackTrace()[offset + 1];

        try
        {
            return Class.forName(element.getClassName());
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }
}
