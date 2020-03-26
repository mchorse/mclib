package mchorse.mclib.config.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

public class ConfigParser
{
	public static JsonObject toJson(Config config)
	{
		JsonObject object = new JsonObject();

		for (Map.Entry<String, ConfigCategory> entry : config.categories.entrySet())
		{
			object.add(entry.getKey(), entry.getValue().toJSON());
		}

		return object;
	}

	public static boolean fromJson(Config config, File file)
	{
		if (!file.exists())
		{
			config.save(file);

			return false;
		}

		try
		{
			JsonObject object = (JsonObject) new JsonParser().parse(FileUtils.readFileToString(file, Charset.defaultCharset()));

			for (Map.Entry<String, ConfigCategory> entry : config.categories.entrySet())
			{
				entry.getValue().fromJSON(object.getAsJsonObject(entry.getKey()));
			}

			return true;
		}
		catch (Exception e)
		{}

		return false;
	}
}