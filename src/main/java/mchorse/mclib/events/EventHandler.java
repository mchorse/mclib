package mchorse.mclib.events;

import mchorse.mclib.network.mclib.client.ClientHandlerTimeSync;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EventHandler
{
    @SubscribeEvent
    public void playerTickEvent(PlayerTickEvent event)
    {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.START && !ClientHandlerTimeSync.isSet() && ClientHandlerTimeSync.canPing())
        {
            ClientHandlerTimeSync.requestServerTime();
        }
    }

    @SubscribeEvent
    public void clientLogsOut(ClientDisconnectionFromServerEvent event)
    {
        ClientHandlerTimeSync.resetTimes();
    }
}
