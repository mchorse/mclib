package mchorse.mclib.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.mclib.config.values.IConfigValue;
import mchorse.mclib.config.values.Value;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class ConfigCategory
{
	public final String id;
	public Config config;
	public final Map<String, IConfigValue> values = new LinkedHashMap<String, IConfigValue>();

	public ConfigCategory(String id)
	{
		this.id = id;
	}

	@SideOnly(Side.CLIENT)
	public String getTitle()
	{
		return this.config.getCategoryTitle(this.id);
	}

	@SideOnly(Side.CLIENT)
	public String getTooltip()
	{
		return this.config.getCategoryTooltip(this.id);
	}

	public void register(String id, IConfigValue value)
	{
		if (value instanceof Value)
		{
			((Value) value).category = this;
		}

		this.values.put(id, value);
	}

	public boolean isVisible()
	{
		for (IConfigValue value : this.values.values())
		{
			if (value.isVisible())
			{
				return true;
			}
		}

		return false;
	}

	public JsonObject toJSON()
	{
		JsonObject object = new JsonObject();

		for (IConfigValue value : this.values.values())
		{
			object.add(value.getId(), value.toJSON());
		}

		return object;
	}

	public void fromJSON(JsonObject object)
	{
		for (Map.Entry<String, JsonElement> entry : object.entrySet())
		{
			IConfigValue value = this.values.get(entry.getKey());

			if (value != null)
			{
				value.reset();
				value.fromJSON(entry.getValue());
			}
		}
	}
}