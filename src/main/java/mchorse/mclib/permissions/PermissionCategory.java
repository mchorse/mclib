package mchorse.mclib.permissions;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IByteBufSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.ArrayList;
import java.util.List;

public class PermissionCategory implements IByteBufSerializable
{
    private String name;
    private final List<PermissionCategory> children = new ArrayList<>();
    private PermissionCategory parent;
    /**
     * If null, then this permission takes the level of the parent
     */
    private DefaultPermissionLevel level;

    public PermissionCategory(String name)
    {
        this(name, null);
    }

    public PermissionCategory(String name, DefaultPermissionLevel level)
    {
        this.name = name;
        this.level = level;
    }

    private PermissionCategory()
    {

    }

    public void addChild(PermissionCategory category)
    {
        this.children.add(category);
        category.parent = this;
    }

    public boolean hasChildren()
    {
        return !this.children.isEmpty();
    }

    public List<PermissionCategory> getChildren()
    {
        return new ArrayList<>(this.children);
    }

    /**
     * Gets the default permission based on this or the parent's default permission level.
     * @return the permission level of this permission or if null of the parent.
     *         If no parent above has a default permission level, this method will return {@link DefaultPermissionLevel#OP}
     */
    public DefaultPermissionLevel getDefaultPermission()
    {
        return (this.parent != null) ? this.parent.getDefaultPermission() : DefaultPermissionLevel.OP;
    }

    @Override
    public String toString()
    {
        return (this.parent != null ? this.parent + "." : "") + this.name;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.name = ByteBufUtils.readUTF8String(buffer);
        this.level = DefaultPermissionLevel.values()[buffer.readInt()];

        int size = buffer.readInt();

        for (int i = 0; i < size; i++)
        {
            PermissionCategory child = new PermissionCategory();

            child.fromBytes(buffer);

            this.addChild(child);
        }

        if (buffer.readBoolean())
        {
            this.parent = new PermissionCategory();

            this.parent.fromBytes(buffer);
        }
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, this.name);
        buffer.writeInt(this.level.ordinal());

        buffer.writeInt(this.children.size());

        for (PermissionCategory category : this.children)
        {
            category.toBytes(buffer);
        }

        buffer.writeBoolean(this.parent != null);

        if (this.parent != null)
        {
            this.parent.toBytes(buffer);
        }
    }
}
