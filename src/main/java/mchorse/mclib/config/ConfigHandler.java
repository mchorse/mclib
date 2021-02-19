package mchorse.mclib.config;

import mchorse.mclib.McLib;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ConfigHandler
{
    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        MinecraftServer server = event.player.getServer();

        if (server == null || server.isSinglePlayer() || !(event.player instanceof EntityPlayerMP))
        {
            return;
        }

        for (Config config : McLib.proxy.configs.modules.values())
        {
            if (config.hasSyncable())
            {
                Dispatcher.sendTo(new PacketConfig(config.filterSyncable(), true), (EntityPlayerMP) event.player);
            }
        }
    }
}
