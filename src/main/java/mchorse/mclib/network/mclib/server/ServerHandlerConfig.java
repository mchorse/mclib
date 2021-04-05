package mchorse.mclib.network.mclib.server;

import mchorse.mclib.McLib;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigManager;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.network.mclib.common.PacketConfig;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;

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

            if (present.hasSyncable())
            {
                ConfigManager.synchronizeConfig(present.filterSyncable(), player.getServerWorld().getMinecraftServer(), null);
            }
        }
    }
}