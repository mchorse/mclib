package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ValueInt extends Value
{
	private int value;
	private int defaultValue;
	private int min = Integer.MIN_VALUE;
	private int max = Integer.MAX_VALUE;
	private boolean color;

	public ValueInt(String id, int defaultValue)
	{
		super(id);

		this.defaultValue = defaultValue;

		this.reset();
	}

	public ValueInt(String id, int defaultValue, int min, int max)
	{
		super(id);

		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;

		this.reset();
	}

	public int get()
	{
		return this.value;
	}

	public void set(int value)
	{
		this.value = MathUtils.clamp(value, this.min, this.max);
	}

	public void setColorValue(String value)
	{
		int v = ColorUtils.parseColor(value);

		if (v != 0)
		{
			this.set(v);
		}
	}

	public void color()
	{
		this.color = true;
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

		if (this.color)
		{
			GuiColorElement color = new GuiColorElement(mc, (value) ->
			{
				this.set(value);
				save.accept(this);
			});

			color.picker.setColor(this.value);
			color.flex().parent(element.area).wh(90, 20).x(1, 0).anchor(1, 0);

			element.add(color);
		}
		else
		{
			GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, (value) ->
			{
				this.set(value.intValue());
				save.accept(this);
			});

			trackpad.flex().parent(element.area).set(90, 0, 90, 20);
			trackpad.limit(this.min, this.max, true);
			trackpad.setValue(this.value);
			element.add(trackpad);
		}

		return Arrays.asList(element.tooltip(config.getValueTooltip(category.id, this.id), Direction.BOTTOM));
	}

	@Override
	public void fromJSON(JsonElement element)
	{
		this.set(element.getAsInt());
	}

	@Override
	public JsonElement toJSON()
	{
		return new JsonPrimitive(this.value);
	}
}