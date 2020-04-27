package mchorse.mclib.client.gui.utils.keys;

public interface IKey
{
	public static final IKey EMPTY = new StringKey("");

	public static IKey lang(String key)
	{
		return new LangKey(key);
	}

	public static IKey str(String key)
	{
		return new StringKey(key);
	}

	public String get();

	public void set(String string);
}