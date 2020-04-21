package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ValueBoolean extends Value
{
	private boolean value;
	private boolean defaultValue;

	public ValueBoolean(String id, boolean defaultValue)
	{
		super(id);

		this.defaultValue = defaultValue;

		this.reset();
	}

	public boolean get()
	{
		return this.value;
	}

	public void set(boolean value)
	{
		this.value = value;
	}

	@Override
	public void reset()
	{
		this.set(this.defaultValue);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiElement> getFields(Minecraft mc, GuiConfig gui, Consumer<IConfigValue> save)
	{
		GuiToggleElement checkbox = new GuiToggleElement(mc, this.getTitle(), this.value, (value) ->
		{
			this.set(value.isToggled());
			save.accept(this);
		});

		return Arrays.asList(checkbox.tooltip(this.getTooltip()));
	}

	@Override
	public void fromJSON(JsonElement element)
	{
		this.set(element.getAsBoolean());
	}

	@Override
	public JsonElement toJSON()
	{
		return new JsonPrimitive(this.value);
	}
}
