package mchorse.mclib.config.values;

import mchorse.mclib.config.ConfigCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Value implements IConfigValue
{
    public final String id;
    public ConfigCategory category;
    private boolean visible = true;

    public Value(String id)
    {
        this.id = id;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    public void invisible()
    {
        this.visible = false;
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
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
}