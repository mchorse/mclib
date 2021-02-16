package mchorse.mclib.network;

import io.netty.buffer.ByteBuf;

public interface IByteBufSerializable
{
    public void fromBytes(ByteBuf buffer);

    public void toBytes(ByteBuf buffer);
}
