package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.resizers.layout.RowResizer;
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
	public final double min;
	public final double max;

	public ValueDouble(String id, double defaultValue)
	{
		super(id);

		this.defaultValue = defaultValue;
		this.min = Double.NEGATIVE_INFINITY;
		this.max = Double.POSITIVE_INFINITY;

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
		GuiLabel label = new GuiLabel(mc, this.getTitle()).anchor(0, 0.5F);
		GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);

		trackpad.flex().w(90);

		element.flex().row(0).preferred(0).height(20);
		element.add(label, trackpad.removeTooltip());

		return Arrays.asList(element.tooltip(this.getTooltip()));
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