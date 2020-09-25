package mchorse.mclib.client.gui.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.config.values.Value;
import mchorse.mclib.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ValueColors extends Value
{
	public List<Color> colors = new ArrayList<Color>();

	public ValueColors(String id)
	{
		super(id);
	}

	@Override
	public void reset()
	{
		this.colors.clear();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiElement> getFields(Minecraft mc, GuiConfig gui)
	{
		return null;
	}

	@Override
	public void fromJSON(JsonElement element)
	{
		if (!element.isJsonArray())
		{
			return;
		}

		JsonArray array = element.getAsJsonArray();

		for (JsonElement color : array)
		{
			if (color.isJsonPrimitive())
			{
				this.colors.add(new Color().set(color.getAsInt(), true));
			}
		}
	}

	@Override
	public JsonElement toJSON()
	{
		JsonArray array = new JsonArray();

		for (Color color : this.colors)
		{
			array.add(new JsonPrimitive(color.getRGBAColor()));
		}

		return array;
	}
}