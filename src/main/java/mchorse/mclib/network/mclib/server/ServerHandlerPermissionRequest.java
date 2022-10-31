package mchorse.mclib.network.mclib.server;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.network.mclib.client.ClientHandlerAnswer;
import mchorse.mclib.network.mclib.common.PacketRequestPermissions;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.HashMap;
import java.util.Map;

public class ServerHandlerPermissionRequest extends ServerMessageHandler<PacketRequestPermissions>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestPermissions message)
    {
        HashMap<String, Boolean> results = new HashMap<>();

        for (String permission : message.getPermissionRequests())
        {
            results.put(permission, PermissionAPI.hasPermission(player, permission));
        }

        ClientHandlerAnswer.sendAnswerTo(player, message.getAnswer(results));
    }
}
