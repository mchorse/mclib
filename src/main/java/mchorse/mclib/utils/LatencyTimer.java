package mchorse.mclib.utils;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IByteBufSerializable;

/**
 * This class is used to clock the time it takes to
 * send something between server and client to sync data like audio.
 *
 * @author Christian F. (known as Chryfi)
 */
public class LatencyTimer implements IByteBufSerializable
{
    private long startTime;
    private long endTime;

    /**
     * Saves the system time it has been created
     */
    public LatencyTimer()
    {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Sets the endTime, if it has not been set yet, to the current system time in milliseconds.
     */
    public void finish()
    {
        if (this.endTime == 0)
        {
            this.endTime = System.currentTimeMillis();
        }
    }

    /**
     * @param raw when true it just calculates the elapsed time,
     *            when false it takes into account the difference between server and client time from ClientHandlerTimeSync class.
     * @return the elapsed time in milliseconds since the creation of this object
     * or if this timer has finished, the elapsed time from start to end.
     */
    public long getElapsedTime()
    {
        return Math.abs((this.endTime != 0) ? (this.endTime - this.startTime) : (System.currentTimeMillis() - this.startTime));
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        LatencyTimer timer = new LatencyTimer();

        timer.startTime = buf.readLong();
        timer.endTime = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(this.startTime);
        buf.writeLong(this.endTime);
    }
}
