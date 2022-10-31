package mchorse.mclib.network.mclib.common;

import com.jcraft.jogg.Packet;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.permissions.PermissionCategory;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class PacketRequestPermissions implements IAnswerRequest<HashMap<String, Boolean>>
{
    private Set<String> requests = new HashSet<>();
    private int callbackID = -1;

    public PacketRequestPermissions()
    {

    }

    public PacketRequestPermissions(int callbackID, Set<String> requests)
    {
        this.callbackID = callbackID;
        this.requests = requests;
    }

    public PacketRequestPermissions(int callbackID, String... requests)
    {
        this(callbackID, new HashSet<>(Arrays.asList(requests)));
    }

    public List<String> getPermissionRequests()
    {
        return new ArrayList<>(this.requests);
    }

    @Override
    public void setCallbackID(int callbackID)
    {
        this.callbackID = callbackID;
    }

    @Override
    public Optional<Integer> getCallbackID()
    {
        return Optional.of(this.callbackID == -1 ? null : this.callbackID);
    }

    @Override
    public PacketAnswer<HashMap<String, Boolean>> getAnswer(HashMap<String, Boolean> value) throws NoSuchElementException
    {
        if (!this.getCallbackID().isPresent())
        {
            throw new NoSuchElementException();
        }

        return new PacketAnswer<>(this.getCallbackID().get(), value);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.callbackID = buf.readInt();

        int size = buf.readInt();

        for (int i = 0; i < size; i++)
        {
            this.requests.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callbackID);
        buf.writeInt(this.requests.size());

        for (String request : this.requests)
        {
            ByteBufUtils.writeUTF8String(buf, request);
        }
    }
}
