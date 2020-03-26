package mchorse.mclib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import mchorse.mclib.config.json.ConfigParser;
import mchorse.mclib.config.values.IConfigValue;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

	/* Translation string related methods */

	@SideOnly(Side.CLIENT)
	public String getTitle()
	{
		return I18n.format(this.id + ".config.title");
	}

	@SideOnly(Side.CLIENT)
	public String getCategoryTitle(String category)
	{
		return I18n.format(this.id + ".config." + category + ".title");
	}

	@SideOnly(Side.CLIENT)
	public String getCategoryTooltip(String category)
	{
		return I18n.format(this.id + ".config." + category + ".tooltip");
	}

	@SideOnly(Side.CLIENT)
	public String getValueTitle(String category, String value)
	{
		return I18n.format(this.id + ".config." + category + "." + value);
	}

	@SideOnly(Side.CLIENT)
	public String getValueTooltip(String category, String value)
	{
		return I18n.format(this.id + ".config.comments." + category + "." + value);
	}

	/**
	 * Get a value from category by their ids
	 */
	public IConfigValue get(String category, String value)
	{
		ConfigCategory cat = this.categories.get(category);

		if (cat != null)
		{
			return cat.values.get(value);
		}

		return null;
	}

	/**
	 * Save config to default location
	 */
	public void save()
	{
		this.save(this.file);
	}

	/**
	 * Save config to given file
	 */
	public boolean save(File file)
	{
		try
		{
			FileUtils.writeStringToFile(this.file, this.toJSON());

			return true;
		}
		catch (IOException e)
		{}

		return false;
	}

	/**
	 * Convert this config into JSON string
	 */
	public String toJSON()
	{
		JsonObject object = ConfigParser.toJson(this);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		StringWriter string = new StringWriter();
		JsonWriter writer = new JsonWriter(string);

		writer.setIndent("    ");
		gson.toJson(object, writer);

		return string.toString();
	}
}