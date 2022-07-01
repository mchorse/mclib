package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.utils.MathUtils;

import javax.annotation.Nonnull;

public abstract class GenericNumberValue<T extends Number & Comparable<T>> extends GenericValue<T>
{
    protected T min;
    protected T max;

    public GenericNumberValue(String id, @Nonnull T defaultValue, @Nonnull T min, @Nonnull T max)
    {
        super(id, defaultValue);

        this.min = min;
        this.max = max;
    }

    @Override
    public void set(T value)
    {
        this.value = MathUtils.clamp((value == null) ? this.getNullValue() : value, this.min, this.max);

        this.saveLater();
    }

    public T getMin()
    {
        return this.min;
    }

    public T getMax()
    {
        return this.max;
    }

    @Override
    protected abstract T getNullValue();

    @Override
    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value);
    }
}
