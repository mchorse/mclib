package mchorse.mclib.network.mclib.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketTime implements IMessage
{
    private long time;

    public long getTime()
    {
        return this.time;
    }

    public PacketTime()
    { }

    public PacketTime(long time)
    {
        this.time = time;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.time = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(this.time);
    }
}