package mchorse.mclib.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityUtils
{
    @SideOnly(Side.CLIENT)
    public static GameType getGameMode()
    {
        return getGameMode(Minecraft.getMinecraft().player);
    }

    @SideOnly(Side.CLIENT)
    public static GameType getGameMode(EntityPlayer player)
    {
        NetworkPlayerInfo networkplayerinfo = EntityUtils.getNetworkInfo(player);

        return networkplayerinfo == null ? GameType.SURVIVAL : networkplayerinfo.getGameType();
    }

    @SideOnly(Side.CLIENT)
    public static boolean isAdventureMode(EntityPlayer player)
    {
        NetworkPlayerInfo info = getNetworkInfo(player);

        return info != null && info.getGameType() == GameType.ADVENTURE;
    }

    @SideOnly(Side.CLIENT)
    public static NetworkPlayerInfo getNetworkInfo(EntityPlayer player)
    {
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();

        if (connection == null)
        {
            return null;
        }

        return connection.getPlayerInfo(player.getGameProfile().getId());
    }
}
