package mchorse.mclib.network.mclib.client;

import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.mclib.common.PacketTime;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * This class is used to compare the time of the server and the time of the client,
 * because of different timezones or changed clock settings
 * The information is needed to sync things like audio between server and client,
 * the difference between client and server time will be used to clean the clocked time from sending data from the server to the client
 *
 * This class is only supposed to have values on the client side.
 *
 * @author Christian F. (known as Chryfi)
 */
public class ClientHandlerTimeSync extends ClientMessageHandler<PacketTime>
{
    private static long serverTime;
    private static long clientTime;

    public static void clockClientTime()
    {
        clientTime = System.currentTimeMillis();
    }

    public static long getClientTime()
    {
        return clientTime;
    }

    public static long getServerTime()
    {
        return serverTime;
    }

    /**
     * @return Math.abs(clientTime - serverTime)
     */
    public static long getClientServerDifference()
    {
        return Math.abs(ClientHandlerTimeSync.getClientTime() - ClientHandlerTimeSync.getServerTime());
    }

    public static void resetTimes()
    {
        serverTime = 0;
        clientTime = 0;
    }

    @Override
    public void run(EntityPlayerSP entityPlayerSP, PacketTime packetTime)
    {
        serverTime = packetTime.getTime();
    }
}
