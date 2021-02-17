package mchorse.mclib.config.values;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.config.ConfigCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Value implements IConfigValue
{
    public final String id;
    public ConfigCategory category;
    private boolean visible = true;
    private boolean clientSide;
    private boolean syncable;

    public Value(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    public Value invisible()
    {
        this.visible = false;

        return this;
    }

    public Value clientSide()
    {
        this.clientSide = true;

        return this;
    }

    public Value syncable()
    {
        this.syncable = true;

        return this;
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }

    @Override
    public boolean isClientSide()
    {
        return this.clientSide;
    }

    @Override
    public boolean isSyncable()
    {
        return this.syncable;
    }

    public void saveLater()
    {
        if (this.category != null && this.category.config != null)
        {
            this.category.config.saveLater();
        }
    }

    @SideOnly(Side.CLIENT)
    public String getTitle()
    {
        return this.category.config.getValueTitle(this.category.id, this.id);
    }

    @SideOnly(Side.CLIENT)
    public String getTitleKey()
    {
        return this.category.config.getValueTitleKey(this.category.id, this.id);
    }

    @SideOnly(Side.CLIENT)
    public String getTooltip()
    {
        return this.category.config.getValueTooltip(this.category.id, this.id);
    }

    @SideOnly(Side.CLIENT)
    public String getTooltipKey()
    {
        return this.category.config.getValueTooltipKey(this.category.id, this.id);
    }

    @Override
    public void copy(IConfigValue value)
    {}

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.visible = buffer.readBoolean();
        this.clientSide = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeBoolean(this.visible);
        buffer.writeBoolean(this.clientSide);
    }
}