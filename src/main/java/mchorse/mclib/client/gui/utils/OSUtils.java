package mchorse.mclib.client.gui.utils;

/**
 * Referenced from OptiFine
 */
public class OSUtils
{
    public static final int EnumOS_UNKNOWN = 0;
    public static final int EnumOS_WINDOWS = 1;
    public static final int EnumOS_OSX = 2;
    public static final int EnumOS_SOLARIS = 3;
    public static final int EnumOS_LINUX = 4;

    public static int getOSType()
    {
        String s = System.getProperty("os.name").toLowerCase();

        if (s.contains("win"))
        {
            return EnumOS_WINDOWS;
        }
        else if (s.contains("mac"))
        {
            return EnumOS_OSX;
        }
        else if (s.contains("solaris"))
        {
            return EnumOS_SOLARIS;
        }
        else if (s.contains("sunos"))
        {
            return EnumOS_SOLARIS;
        }
        else if (s.contains("linux"))
        {
            return EnumOS_LINUX;
        }
        else
        {
            return s.contains("unix") ? EnumOS_LINUX : EnumOS_UNKNOWN;
        }
    }
}
