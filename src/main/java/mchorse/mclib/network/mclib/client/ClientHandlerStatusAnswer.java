package mchorse.mclib.network.mclib.client;

import mchorse.mclib.network.mclib.common.PacketAnswer;
import mchorse.mclib.network.mclib.common.PacketStatusAnswer;
import net.minecraft.client.entity.EntityPlayerSP;

//TODO write a factory to abstract packet answers so only one answer class gets sent and registered
public class ClientHandlerStatusAnswer extends ClientHandlerAnswer<PacketStatusAnswer>
{
    @Override
    public void run(EntityPlayerSP player, PacketStatusAnswer packet)
    {
        consume(packet.getCallbackID(), packet.getValue());
    }
}
