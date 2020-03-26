package mchorse.mclib.client.gui.utils;

public class Label<T>
{
	public String title;
	public T value;

	public Label(String title, T value)
	{
		this.title = title;
		this.value = value;
	}
}
