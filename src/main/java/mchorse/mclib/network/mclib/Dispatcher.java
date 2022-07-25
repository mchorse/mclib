package mchorse.mclib.network.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.network.AbstractDispatcher;
import mchorse.mclib.network.mclib.client.ClientHandlerConfig;
import mchorse.mclib.network.mclib.client.ClientHandlerConfirm;
import mchorse.mclib.network.mclib.client.ClientHandlerTimeSync;
import mchorse.mclib.network.mclib.common.PacketConfig;
import mchorse.mclib.network.mclib.common.PacketConfirm;
import mchorse.mclib.network.mclib.common.PacketDropItem;
import mchorse.mclib.network.mclib.common.PacketRequestConfigs;
import mchorse.mclib.network.mclib.common.PacketTime;
import mchorse.mclib.network.mclib.server.ServerHandlerConfig;
import mchorse.mclib.network.mclib.server.ServerHandlerConfirm;
import mchorse.mclib.network.mclib.server.ServerHandlerDropItem;
import mchorse.mclib.network.mclib.server.ServerHandlerRequestConfigs;
import mchorse.mclib.network.mclib.server.ServerHandlerTimeSync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class Dispatcher
{
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(McLib.MOD_ID)
    {
        @Override
        public void register()
        {
            register(PacketDropItem.class, ServerHandlerDropItem.class, Side.SERVER);

            /* Config related packets */
            register(PacketRequestConfigs.class, ServerHandlerRequestConfigs.class, Side.SERVER);
            register(PacketConfig.class, ServerHandlerConfig.class, Side.SERVER);
            register(PacketConfig.class, ClientHandlerConfig.class, Side.CLIENT);

            /* Confirm related packets */
            register(PacketConfirm.class, ClientHandlerConfirm.class, Side.CLIENT);
            register(PacketConfirm.class, ServerHandlerConfirm.class, Side.SERVER);

            /* to let the client know what time the server has */
            register(PacketTime.class, ClientHandlerTimeSync.class, Side.CLIENT);
            register(PacketTime.class, ServerHandlerTimeSync.class, Side.SERVER);
        }
    };

    /**
     * Send message to players who are tracking given entity
     */
    public static void sendToTracked(Entity entity, IMessage message)
    {
        DISPATCHER.sendToTracked(entity, message);
    }

    /**
     * Send message to given player
     */
    public static void sendTo(IMessage message, EntityPlayerMP player)
    {
        DISPATCHER.sendTo(message, player);
    }

    /**
     * Send message to the server
     */
    public static void sendToServer(IMessage message)
    {
        DISPATCHER.sendToServer(message);
    }

    /**
     * Register all the networking messages and message handlers
     */
    public static void register()
    {
        DISPATCHER.register();
    }
}