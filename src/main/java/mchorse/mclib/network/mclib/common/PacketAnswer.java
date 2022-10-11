package mchorse.mclib.network.mclib.common;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketAnswer implements IMessage
{
    private int callBackID;

    /* allow subclasses to declare default constructor since Forge wants it for packets */
    public PacketAnswer()
    {

    }

    public PacketAnswer(int callBackID)
    {
        this.callBackID = callBackID;
    }

    public int getCallbackID()
    {
        return this.callBackID;
    }

    public abstract Object[] getValue();

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.callBackID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callBackID);
    }
}
