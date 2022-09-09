package mchorse.mclib.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Network dispatcher
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class AbstractDispatcher
{
    private final SimpleNetworkWrapper dispatcher;
    private byte nextPacketID;

    public AbstractDispatcher(String modID)
    {
        this.dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(modID);
    }

    public SimpleNetworkWrapper get()
    {
        return this.dispatcher;
    }

    /**
     * Here you supposed to register packets to handlers 
     */
    public abstract void register();

    /**
     * Send message to players who are tracking given entity
     */
    public void sendToTracked(Entity entity, IMessage message)
    {
        EntityTracker tracker = ((WorldServer) entity.world).getEntityTracker();

        for (EntityPlayer player : tracker.getTrackingPlayers(entity))
        {
            this.dispatcher.sendTo(message, (EntityPlayerMP) player);
        }
    }

    /**
     * Send message to given player
     */
    public void sendTo(IMessage message, EntityPlayerMP player)
    {
        this.dispatcher.sendTo(message, player);
    }

    /**
     * Send message to all players
     * @param message
     */
    public void sendToAll(IMessage message)
    {
        this.dispatcher.sendToAll(message);
    }

    /**
     * Send message to all players around the given point
     * @param message
     * @param point The {@link NetworkRegistry.TargetPoint} around which to send
     */
    public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point)
    {
        this.dispatcher.sendToAllAround(message, point);
    }

    /**
     * Send message to the server
     */
    public void sendToServer(IMessage message)
    {
        this.dispatcher.sendToServer(message);
    }

    /**
     * Register given message with given message handler on a given side
     */
    public <REQ extends IMessage, REPLY extends IMessage> void register(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, Side side)
    {
        this.dispatcher.registerMessage(handler, message, this.nextPacketID++, side);
    }
}