package mchorse.mclib.utils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OpHelper
{
    @SideOnly(Side.CLIENT)
    public static int getPlayerOpLevel()
    {
        return Minecraft.getMinecraft().player.getPermissionLevel();
    }

    @SideOnly(Side.CLIENT)
    public static boolean isPlayerOp()
    {
        return isOp(getPlayerOpLevel());
    }

    public static boolean isOp(int opLevel)
    {
        return opLevel >= 2;
    }
}
