package mchorse.mclib.network.mclib.server;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketTime;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Calendar;
import java.util.TimeZone;

public class ServerHandlerTimeSync extends ServerMessageHandler<PacketTime>
{
    @Override
    public void run(EntityPlayerMP player, PacketTime message)
    {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        Calendar c = Calendar.getInstance(tz);

        long time = c.getTimeInMillis() - System.currentTimeMillis();
        time = (Math.abs(time) < 100) ? 0 : time;

        Dispatcher.sendTo(new PacketTime(time), player);
    }
}
