package mchorse.mclib.permissions;

import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.client.ClientHandlerAnswer;
import mchorse.mclib.network.mclib.common.PacketRequestPermission;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import java.util.function.Consumer;

public class PermissionUtils
{
    /**
     * Side independent method for checking if it has the permission and then passing the result to the callback
     * @param node the full name of the permission
     * @param player the player to check for the permission
     * @param callback the callback to be executed after checking the player
     *                 or after the server has sent the permission result.
     */
    public static void hasPermission(EntityPlayer player, PermissionCategory permission, Consumer<Boolean> callback)
    {
        if (!Minecraft.getMinecraft().world.isRemote)
        {
            callback.accept(permission.playerHasPermission(player));
        }
        else
        {
            ClientHandlerAnswer.requestServerAnswer(Dispatcher.DISPATCHER, new PacketRequestPermission(-1, permission), callback);
        }
    }
}
