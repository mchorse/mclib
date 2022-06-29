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
     * Only used in Aperture undo/redo system
     * @param value if the specified object is not null and assignable to the generic type T,
     *             the value be set
     */
    @Override
    public void setValue(Object value)
    {
        if (value == null)
        {
            return;
        }

        try
        {
            this.set((T) value);
        }
        catch(ClassCastException e)
        { }
    }

    /**
     * Only used in Aperture undo/redo system
     * @return this.get()
     */
    @Override
    public Object getValue()
    {
        return this.get();
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
     *         Or if the specified object or its value and this.value are both null.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof GenericValue))
        {
            /*
             * if this.value == null and obj is null return true because both values are null
             */
            return (obj == null && this.value == null);
        }

        GenericValue valueObj = (GenericValue) obj;

        if (valueObj.value == null && this.value == null)
        {
            return true;
        }
        else if (valueObj.value == null)
        {
            return false;
        }
        else if (valueObj.value.equals(this.value))
        {
            return true;
        }

        return super.equals(obj);
    }

    public boolean hasChanged()
    {
        return !this.value.equals(this.defaultValue);
    }
}
