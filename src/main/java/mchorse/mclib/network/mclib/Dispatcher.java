package mchorse.mclib.network.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.network.AbstractDispatcher;
import mchorse.mclib.network.mclib.client.ClientHandlerAnswer;
import mchorse.mclib.network.mclib.client.ClientHandlerBoolean;
import mchorse.mclib.network.mclib.client.ClientHandlerConfig;
import mchorse.mclib.network.mclib.client.ClientHandlerConfirm;
import mchorse.mclib.network.mclib.common.PacketAnswer;
import mchorse.mclib.network.mclib.common.PacketBoolean;
import mchorse.mclib.network.mclib.common.PacketConfig;
import mchorse.mclib.network.mclib.common.PacketConfirm;
import mchorse.mclib.network.mclib.common.PacketDropItem;
import mchorse.mclib.network.mclib.common.PacketRequestConfigs;
import mchorse.mclib.network.mclib.common.PacketRequestPermission;
import mchorse.mclib.network.mclib.server.ServerHandlerConfig;
import mchorse.mclib.network.mclib.server.ServerHandlerConfirm;
import mchorse.mclib.network.mclib.server.ServerHandlerDropItem;
import mchorse.mclib.network.mclib.server.ServerHandlerPermissionRequest;
import mchorse.mclib.network.mclib.server.ServerHandlerRequestConfigs;
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

            //TODO abstract confirm thing into server to client to server answer thing - see IAnswerRequest etc.
            /* Confirm related packets */
            register(PacketConfirm.class, ClientHandlerConfirm.class, Side.CLIENT);
            register(PacketConfirm.class, ServerHandlerConfirm.class, Side.SERVER);

            /* client answer related packets */
            register(PacketAnswer.class, ClientHandlerAnswer.class, Side.CLIENT);
            register(PacketBoolean.class, ClientHandlerBoolean.class, Side.CLIENT);

            register(PacketRequestPermission.class, ServerHandlerPermissionRequest.class, Side.SERVER);
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