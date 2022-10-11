package mchorse.mclib.network.mclib.common;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.keys.KeyParser;

public class PacketStatusAnswer extends PacketAnswer
{
    private IKey message;
    private boolean status;

    public PacketStatusAnswer()
    { }

    public PacketStatusAnswer(int callBackID, IKey message, boolean status)
    {
        super(callBackID);

        this.message = message;
        this.status = status;
    }

    @Override
    public Object[] getValue()
    {
        return new Object[]{this.message, this.status};
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        super.fromBytes(buf);

        this.message = KeyParser.keyFromBytes(buf);
        this.status = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        super.toBytes(buf);

        KeyParser.keyToBytes(buf, this.message);
        buf.writeBoolean(this.status);
    }
}
