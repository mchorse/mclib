package mchorse.mclib.config.values;

public abstract class Value implements IConfigValue
{
	public final String id;

	public Value(String id)
	{
		this.id = id;
	}

	@Override
	public String getId()
	{
		return this.id;
	}
}