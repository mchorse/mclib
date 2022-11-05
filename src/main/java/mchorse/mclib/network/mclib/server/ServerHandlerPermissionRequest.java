package mchorse.mclib.network.mclib.server;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.network.mclib.client.ClientHandlerAnswer;
import mchorse.mclib.network.mclib.common.PacketRequestPermission;
import mchorse.mclib.permissions.PermissionCategory;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerPermissionRequest extends ServerMessageHandler<PacketRequestPermission>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestPermission message)
    {
        PermissionCategory perm = message.getPermissionRequest();

        boolean hasPermission = perm != null && perm.playerHasPermission(player);

        ClientHandlerAnswer.sendAnswerTo(player, message.getAnswer(hasPermission));
    }
}
