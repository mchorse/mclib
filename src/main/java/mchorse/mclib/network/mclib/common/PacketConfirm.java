package mchorse.mclib.network.mclib.common;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.keys.KeyParser;
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
    public IKey langKey;
    public boolean confirm;

    public PacketConfirm(ClientHandlerConfirm.GUI gui, IKey langKey, Consumer<Boolean> callback)
    {
        this.gui = gui;
        this.langKey = langKey;

        Map.Entry<Integer, Consumer<Boolean>> entry = ServerHandlerConfirm.getLastConsumerEntry();
        this.consumerID = (entry != null) ? entry.getKey()+1 : 0;

        ServerHandlerConfirm.addConsumer(consumerID, callback);
    }

    public PacketConfirm()
    {}

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.langKey = KeyParser.keyFromBytes(buf);
        this.gui = ClientHandlerConfirm.GUI.values()[buf.readInt()];
        this.consumerID = buf.readInt();
        this.confirm = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        KeyParser.keyToBytes(buf, this.langKey);
        buf.writeInt(this.gui.ordinal());
        buf.writeInt(this.consumerID);
        buf.writeBoolean(this.confirm);
    }
}