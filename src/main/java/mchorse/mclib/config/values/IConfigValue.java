package mchorse.mclib.config.values;

import com.google.gson.JsonElement;

public interface IConfigValue
{
	public String getId();

	public void reset();

	public void fromJSON(JsonElement element);

	public JsonElement toJSON();
}