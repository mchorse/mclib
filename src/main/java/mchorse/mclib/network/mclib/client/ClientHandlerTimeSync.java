package mchorse.mclib.network.mclib.client;

import mchorse.mclib.McLib;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketTime;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.Calendar;
import java.util.TimeZone;

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
    /**
     * Difference between server and UTC time
     */
    private static long serverTime = -1;
    /**
     * Difference between client and UTC time
     */
    private static long clientTime = -1;
    /**
     * The time when the next ping is allowed
     */
    private static long randomBackoffTime;

    @Override
    public void run(EntityPlayerSP entityPlayerSP, PacketTime packetTime)
    {
        serverTime = packetTime.getTime();

        McLib.LOGGER.info("Received server UTC time difference. Client server time difference: " + getClientServerDifference() + " ms");
    }

    /**
     * @return Math.abs(clientTime - serverTime)
     */
    public static long getClientServerDifference()
    {
        return Math.abs(clientTime - serverTime);
    }

    /**
     * Checks if the serverTime and clientTime are set
     * @return true if server and client time are set
     */
    public static boolean isSet()
    {
        return serverTime != -1 && clientTime != -1;
    }

    public static void resetTimes()
    {
        serverTime = -1;
        clientTime = -1;
    }

    /**
     * Pings the server to get the delay and the server's time in millis. The random backoff time generated in this method is between 100 and 1500 ms.
     * Before calling this, the method {@link #canPing()} may need to be called.
     */
    public static void requestServerTime()
    {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        Calendar c = Calendar.getInstance(tz);

        clientTime = c.getTimeInMillis() - System.currentTimeMillis();
        clientTime = (Math.abs(clientTime) < 100) ? 0 : clientTime;

        McLib.LOGGER.info("Pinging the server with client to UTC time difference: " + clientTime + " ms");

        Dispatcher.sendToServer(new PacketTime());

        randomBackoffTime = System.currentTimeMillis() + Math.round(Math.random() * 1400L) + 100L;
    }

    public static boolean canPing()
    {
        return System.currentTimeMillis() > randomBackoffTime;
    }
}
