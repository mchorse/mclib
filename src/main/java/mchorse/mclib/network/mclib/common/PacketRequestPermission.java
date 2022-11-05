package mchorse.mclib.network.mclib.common;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.McLib;
import mchorse.mclib.permissions.PermissionCategory;

import java.util.NoSuchElementException;
import java.util.Optional;

public class PacketRequestPermission implements IAnswerRequest<Boolean>
{
    private PermissionCategory request;
    private int callbackID = -1;

    public PacketRequestPermission()
    {

    }

    public PacketRequestPermission(int callbackID, PermissionCategory permission)
    {
        this.callbackID = callbackID;
        this.request = permission;
    }

    public PermissionCategory getPermissionRequest()
    {
        return this.request;
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
    public PacketBoolean getAnswer(Boolean value) throws NoSuchElementException
    {
        return new PacketBoolean(this.getCallbackID().get(), value);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.callbackID = buf.readInt();
        this.request = McLib.permissionFactory.getPermission(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callbackID);
        buf.writeInt(McLib.permissionFactory.getPermissionID(this.request));
    }
}
