package mchorse.mclib.network.mclib.server;

import mchorse.mclib.McLib;
import mchorse.mclib.config.Config;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketConfig;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class ServerHandlerConfig extends ServerMessageHandler<PacketConfig>
{
    @Override
    public void run(EntityPlayerMP player, PacketConfig message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        Config present = McLib.proxy.configs.modules.get(message.config.id);

        if (present != null)
        {
            present.copy(message.config);
            present.save();

            MinecraftServer server = player.getServerWorld().getMinecraftServer();

            /* Synchronize back server values */
            if (present.hasSyncable() && server != null)
            {
                for (EntityPlayerMP target : server.getPlayerList().getPlayers())
                {
                    if (target == null)
                    {
                        continue;
                    }

                    Dispatcher.sendTo(new PacketConfig(present, true), target);
                }
            }
        }
    }
}