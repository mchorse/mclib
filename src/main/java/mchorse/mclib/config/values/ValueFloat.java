package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.utils.MathUtils;

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
