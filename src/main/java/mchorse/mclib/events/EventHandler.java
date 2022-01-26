package mchorse.mclib.events;

import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.client.ClientHandlerTimeSync;
import mchorse.mclib.network.mclib.common.PacketTime;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class EventHandler
{

    /**
     * Player login event from the server side
     * @param event
     */
    @SubscribeEvent
    public void playerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        Dispatcher.sendTo(new PacketTime(System.currentTimeMillis()), player);
    }

    /**
     * Client logs into server from client side
     * @param event
     */
    @SubscribeEvent
    public void clientLogsIn(ClientConnectedToServerEvent event)
    {
        ClientHandlerTimeSync.clockClientTime();
    }

    @SubscribeEvent
    public void clientLogsOut(ClientDisconnectionFromServerEvent event)
    {
        ClientHandlerTimeSync.resetTimes();
    }
}
