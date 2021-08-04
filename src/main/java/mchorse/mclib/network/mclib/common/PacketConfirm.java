package mchorse.mclib.network.mclib.common;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.mclib.client.ClientHandlerConfirm;
import mchorse.mclib.network.mclib.server.ServerHandlerConfirm;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Map;
import java.util.function.Consumer;

public class PacketConfirm implements IMessage
{

    public int consumerID;
    public ClientHandlerConfirm.GUI gui;
    public String messageKey;
    public boolean confirm;

    public PacketConfirm(ClientHandlerConfirm.GUI gui, String messageKey, Consumer<Boolean> callback)
    {
        Map.Entry<Integer, Consumer<Boolean>> entry = ServerHandlerConfirm.getLastConsumerEntry();
        this.consumerID = (entry != null) ? entry.getKey()+1 : 0;

        ServerHandlerConfirm.addConsumer(consumerID, callback);

        this.gui = gui;
        this.messageKey = messageKey;
    }

    public PacketConfirm()
    {}

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.gui = ClientHandlerConfirm.GUI.values()[buf.readInt()];
        this.messageKey = ByteBufUtils.readUTF8String(buf);
        this.consumerID = buf.readInt();
        this.confirm = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.gui.ordinal());
        ByteBufUtils.writeUTF8String(buf, this.messageKey);
        buf.writeInt(this.consumerID);
        buf.writeBoolean(this.confirm);
    }
}