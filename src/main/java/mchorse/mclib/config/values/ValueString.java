package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueString extends Value
{
	private String value = "";
	private String defaultValue;

	public ValueString(String id, String defaultValue)
	{
		super(id);

		this.defaultValue = defaultValue;

		this.reset();
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public void reset()
	{
		this.setValue(this.defaultValue);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiElement> getFields(Minecraft mc, Config config, ConfigCategory category)
	{
		GuiLabel label = new GuiLabel(mc, config.getValueTitle(category.id, this.id));
		GuiTextElement textbox = new GuiTextElement(mc, this::setValue);

		label.resizer().set(0, 0, 180, 8);
		textbox.resizer().set(0, 0, 180, 20);
		textbox.setText(this.value);

		return Arrays.asList(label, textbox.tooltip(config.getValueTooltip(category.id, this.id), Direction.BOTTOM));
	}

	@Override
	public void fromJSON(JsonElement element)
	{
		this.setValue(element.getAsString());
	}

	@Override
	public JsonElement toJSON()
	{
		return new JsonPrimitive(this.value);
	}
}