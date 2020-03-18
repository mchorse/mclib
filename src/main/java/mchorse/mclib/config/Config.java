package mchorse.mclib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import mchorse.mclib.config.json.ConfigParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class Config
{
	public final String id;
	public final File file;

	public final Map<String, ConfigCategory> categories = new HashMap<String, ConfigCategory>();

	public Config(String id, File file)
	{
		this.id = id;
		this.file = file;
	}

	public void save()
	{
		this.save(this.file);
	}

	public boolean save(File file)
	{
		JsonObject object = ConfigParser.toJson(this);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		StringWriter string = new StringWriter();
		JsonWriter writer = new JsonWriter(string);

		writer.setIndent("    ");
		gson.toJson(object, writer);

		try
		{
			FileUtils.writeStringToFile(this.file, string.toString());

			return true;
		}
		catch (IOException e)
		{}

		return false;
	}
}