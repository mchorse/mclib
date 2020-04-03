package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ValueDouble extends Value
{
	private double value;
	private double defaultValue;
	private double min = Double.NEGATIVE_INFINITY;
	private double max = Double.POSITIVE_INFINITY;

	public ValueDouble(String id, double defaultValue)
	{
		super(id);

		this.defaultValue = defaultValue;

		this.reset();
	}

	public ValueDouble(String id, double defaultValue, double min, double max)
	{
		super(id);

		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;

		this.reset();
	}

	public double get()
	{
		return this.value;
	}

	public void set(double value)
	{
		this.value = MathUtils.clamp(value, this.min, this.max);
	}

	@Override
	public void reset()
	{
		this.set(this.defaultValue);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiElement> getFields(Minecraft mc, GuiConfig gui, Config config, ConfigCategory category, Consumer<IConfigValue> save)
	{
		GuiElement element = new GuiElement(mc);
		GuiLabel label = new GuiLabel(mc, config.getValueTitle(category.id, this.id)).anchor(0, 0.5F);

		label.flex().parent(element.area).set(0, 0, 90, 20);
		element.flex().set(0, 0, 180, 20);
		element.add(label);

		GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, (v) ->
		{
			this.set(v.doubleValue());
			save.accept(this);
		});

		trackpad.flex().parent(element.area).set(90, 0, 90, 20);
		trackpad.limit((float) this.min, (float) this.max);
		trackpad.setValue((float) this.value);
		element.add(trackpad);

		return Arrays.asList(element.tooltip(config.getValueTooltip(category.id, this.id), Direction.BOTTOM));
	}

	@Override
	public void fromJSON(JsonElement element)
	{
		this.set(element.getAsDouble());
	}

	@Override
	public JsonElement toJSON()
	{
		return new JsonPrimitive(this.value);
	}
}
