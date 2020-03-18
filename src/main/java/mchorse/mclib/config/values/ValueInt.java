package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.utils.MathUtils;

public class ValueInt extends Value
{
	private int value;
	private int defaultValue;
	private int min = Integer.MIN_VALUE;
	private int max = Integer.MAX_VALUE;

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

	public void setValue(int value)
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
		this.setValue(element.getAsInt());
	}

	@Override
	public JsonElement toJSON()
	{
		return new JsonPrimitive(this.value);
	}
}
