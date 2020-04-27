package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfig;
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

	public String get()
	{
		return this.value;
	}

	public void set(String value)
	{
		this.value = value;
		this.saveLater();
	}

	@Override
	public void reset()
	{
		this.set(this.defaultValue);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiElement> getFields(Minecraft mc, GuiConfig gui)
	{
		GuiElement element = new GuiElement(mc);
		GuiLabel label = Elements.label(IKey.lang(this.getTitleKey())).anchor(0, 0.5F);
		GuiTextElement textbox = new GuiTextElement(mc, this);

		textbox.flex().w(90);

		element.flex().row(0).preferred(0).height(20);
		element.add(label, textbox.removeTooltip());

		return Arrays.asList(element.tooltip(IKey.lang(this.getTooltipKey())));
	}

	@Override
	public void fromJSON(JsonElement element)
	{
		this.set(element.getAsString());
	}

	@Override
	public JsonElement toJSON()
	{
		return new JsonPrimitive(this.value);
	}
}