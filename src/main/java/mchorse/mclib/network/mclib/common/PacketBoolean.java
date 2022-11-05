package mchorse.mclib.network.mclib.common;

import io.netty.buffer.ByteBuf;

public class PacketBoolean extends PacketAnswer<Boolean>
{
    public PacketBoolean()
    { }

    public PacketBoolean(int callbackID, boolean value)
    {
        super(callbackID, value);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.callBackID = buf.readInt();
        this.answer = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callBackID);
        buf.writeBoolean(this.getValue());
    }
}
