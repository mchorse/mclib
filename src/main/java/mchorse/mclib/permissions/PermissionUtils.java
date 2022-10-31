package mchorse.mclib.permissions;

import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.client.ClientHandlerAnswer;
import mchorse.mclib.network.mclib.common.PacketRequestPermissions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
    public static void hasPermission(EntityPlayer player, String node, Consumer<Boolean> callback)
    {
        if (!Minecraft.getMinecraft().world.isRemote)
        {
            callback.accept(PermissionAPI.hasPermission(player, node));
        }
        else
        {
            ClientHandlerAnswer.requestServerAnswer(Dispatcher.DISPATCHER, new PacketRequestPermissions(-1, node), (obj) ->
            {
                if (obj instanceof Map)
                {
                    Object[] values = ((Map) obj).values().toArray();

                    if (values.length > 0 && values[0] instanceof Boolean)
                    {
                        callback.accept((Boolean) values[0]);

                        System.out.println("" + values[0]);
                    }
                }
            });
        }
    }

    /**
     * Side independent method for checking if the player has a set of permissions and then passing the result to the callback
     * @param nodes the full name of the permission
     * @param player the player to check for the permission
     * @param callback the callback to be executed after checking the player
     *                 or after the server has sent the permission result.
     */
    public static void hasPermissions(EntityPlayer player, Set<String> nodes, Consumer<HashMap<String, Boolean>> callback)
    {
        if (!Minecraft.getMinecraft().world.isRemote)
        {
            HashMap<String, Boolean> results = new HashMap<>();

            for (String node : nodes)
            {
                results.put(node, PermissionAPI.hasPermission(player, node));
            }

            callback.accept(results);
        }
        else
        {
            ClientHandlerAnswer.requestServerAnswer(Dispatcher.DISPATCHER, new PacketRequestPermissions(-1, nodes), callback);
        }
    }
}
