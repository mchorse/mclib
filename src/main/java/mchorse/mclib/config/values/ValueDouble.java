package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.utils.MathUtils;

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

	public void setValue(double value)
	{
		this.value = MathUtils.clamp(value, this.min, this.max);
	}

	@Override
	public void reset()
	{
		this.setValue(this.defaultValue);
	}

	@Override
	public void fromJSON(JsonElement element)
	{
		this.setValue(element.getAsDouble());
	}

	@Override
	public JsonElement toJSON()
	{
		return new JsonPrimitive(this.value);
	}
}
