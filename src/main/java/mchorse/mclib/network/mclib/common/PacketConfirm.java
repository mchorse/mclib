package mchorse.mclib.network.mclib.common;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.mclib.client.ClientHandlerConfirm;
import mchorse.mclib.network.mclib.server.ServerHandlerConfirm;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

public class PacketConfirm implements IMessage
{

    public int consumerID;
    public ClientHandlerConfirm.GUI gui;
    public String messageKey;
    public String[] args;
    public boolean confirm;

    public PacketConfirm(ClientHandlerConfirm.GUI gui, String messageKey, @Nullable String[] args, Consumer<Boolean> callback)
    {
        this.gui = gui;
        this.messageKey = messageKey;
        this.args = args;

        Map.Entry<Integer, Consumer<Boolean>> entry = ServerHandlerConfirm.getLastConsumerEntry();
        this.consumerID = (entry != null) ? entry.getKey()+1 : 0;

        ServerHandlerConfirm.addConsumer(consumerID, callback);
    }

    public PacketConfirm()
    {}

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int length = buf.readInt();

        this.args = new String[length];

        for(int i = 0; i<length; i++)
        {
            this.args[i] = ByteBufUtils.readUTF8String(buf);
        }

        this.gui = ClientHandlerConfirm.GUI.values()[buf.readInt()];
        this.messageKey = ByteBufUtils.readUTF8String(buf);
        this.consumerID = buf.readInt();
        this.confirm = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.args.length);

        if(this.args != null)
        {
            for(String arg : this.args)
            {
                ByteBufUtils.writeUTF8String(buf, arg);
            }
        }

        buf.writeInt(this.gui.ordinal());
        ByteBufUtils.writeUTF8String(buf, this.messageKey);
        buf.writeInt(this.consumerID);
        buf.writeBoolean(this.confirm);
    }
}