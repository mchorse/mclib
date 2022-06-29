package mchorse.mclib.config.values;

public abstract class GenericValue<T> extends Value
{
    protected T value;
    protected T defaultValue;
    protected T serverValue;

    public GenericValue(String id)
    {
        super(id);
    }

    public GenericValue(String id, T defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;

        this.reset();
    }

    public T get()
    {
        return this.serverValue == null ? this.value : this.serverValue;
    }

    public void set(T value)
    {
        this.value = (value == null) ? this.getNullValue() : value;
        this.saveLater();
    }

    /**
     * @return the default value that this type produces when not being initialised
     */
    protected T getNullValue()
    {
        return null;
    }

    @Override
    public void reset()
    {
        this.set(this.defaultValue);
    }

    /**
     * Compare the objects based on their value. Ignores defaultValue etc.
     * @param obj
     * @return true if this object's value equals the specified object's value, using the equals method of Object class.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof GenericValue))
        {
            /*
             * if this.value == null and obj is null return true because both values are null
             * REVISE
             */
            return (obj == null && this.value != null);
        }

        return ((GenericValue) obj).value.equals(this.value);
    }

    public boolean hasChanged()
    {
        return this.value.equals(this.defaultValue);
    }
}
