package mchorse.mclib.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import java.io.StringWriter;

public class JsonUtils
{
	public static String jsonToPretty(JsonElement element)
	{
		StringWriter writer = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(writer);
		Gson gson = new Gson();

		gson.toJson(element, jsonWriter);
		jsonWriter.setIndent("    ");

		/* Prettify arrays */
		return writer.toString().replaceAll("\\n\\s+(?=-?\\d|\\])", " ");
	}
}