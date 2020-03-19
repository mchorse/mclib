package mchorse.mclib.config.values;

public abstract class Value implements IConfigValue
{
	public final String id;
	private boolean visible = true;

	public Value(String id)
	{
		this.id = id;
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