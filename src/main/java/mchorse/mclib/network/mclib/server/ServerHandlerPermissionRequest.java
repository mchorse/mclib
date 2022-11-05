package mchorse.mclib.network.mclib.server;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.network.mclib.client.ClientHandlerAnswer;
import mchorse.mclib.network.mclib.common.PacketRequestPermission;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerPermissionRequest extends ServerMessageHandler<PacketRequestPermission>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestPermission message)
    {
        boolean hasPermission = message.getPermissionRequest().playerHasPermission(player);

        ClientHandlerAnswer.sendAnswerTo(player, message.getAnswer(hasPermission));
    }
}
