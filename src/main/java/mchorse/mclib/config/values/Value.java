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

	@SideOnly(Side.CLIENT)
	public String getTitle()
	{
		return this.category.config.getValueTitle(this.category.id, this.id);
	}

	@SideOnly(Side.CLIENT)
	public String getTooltip()
	{
		return this.category.config.getValueTooltip(this.category.id, this.id);
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

	@Override
	public String getId()
	{
		return this.id;
	}
}