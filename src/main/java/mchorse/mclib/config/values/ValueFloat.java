package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueFloat extends Value
{
	private float value;
	private float defaultValue;
	private float min = Float.NEGATIVE_INFINITY;
	private float max = Float.POSITIVE_INFINITY;

	public ValueFloat(String id, float defaultValue)
	{
		super(id);

		this.defaultValue = defaultValue;

		this.reset();
	}

	public ValueFloat(String id, float defaultValue, float min, float max)
	{
		super(id);

		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;

		this.reset();
	}

	public void setValue(float value)
	{
		this.value = MathUtils.clamp(value, this.min, this.max);
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
		GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, config.getValueTitle(category.id, this.id), this::setValue);

		trackpad.resizer().set(0, 0, 180, 20);
		trackpad.setLimit(this.min, this.max);
		trackpad.setValue(this.value);

		return Arrays.asList(trackpad.tooltip(config.getValueTooltip(category.id, this.id), Direction.BOTTOM));
	}

	@Override
	public void fromJSON(JsonElement element)
	{
		this.setValue(element.getAsFloat());
	}

	@Override
	public JsonElement toJSON()
	{
		return new JsonPrimitive(this.value);
	}
}
