package mchorse.mclib.network.mclib.server;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.network.mclib.common.PacketConfirm;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.TreeMap;
import java.util.function.Consumer;

public class ServerHandlerConfirm extends ServerMessageHandler<PacketConfirm>
{
    public static TreeMap<Integer, Consumer<Boolean>> consumers = new TreeMap<Integer, Consumer<Boolean>>();

    @Override
    public void run(EntityPlayerMP entityPlayerMP, PacketConfirm packetConfirm)
    {
        if (consumers.containsKey(packetConfirm.confirmId))
        {
            consumers.remove(packetConfirm.confirmId).accept(packetConfirm.confirm);
        }
    }
}
