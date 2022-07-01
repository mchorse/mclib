package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.ICloneable;
import net.minecraft.nbt.NBTBase;

import javax.annotation.Nullable;

public abstract class GenericValue<T> extends Value implements ICloneable<GenericValue<T>>
{
    protected T value;
    protected T defaultValue;
    protected T serverValue;

    public GenericValue(String id)
    {
        super(id);
    }

    /**
     * Sets defaultValue to the provided defaultValue.
     * If defaultValue is null, the value of {@link #getNullValue()} will be set.
     * After defaultValue has been set the method {@link #reset()} will be called
     * @param id
     * @param defaultValue
     */
    public GenericValue(String id, T defaultValue)
    {
        super(id);

        this.defaultValue = (defaultValue == null) ? this.getNullValue() : defaultValue;

        this.reset();
    }

    /**
     * @return the value of this instance, or if the serverValue is not null it returns the serverValue
     */
    public T get()
    {
        return this.serverValue == null ? this.value : this.serverValue;
    }

    /**
     * Set the value of this instance to the provided value.
     * If the value is null, the result of {@link #getNullValue()} will be assigned.
     * @param value
     */
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
     * @return the value returned by this {@link #get()}
     */
    @Override
    public Object getValue()
    {
        return this.get();
    }

    /**
     * @return the default value that this type produces when not being initialised.
     *         This is used in {@link #set(T)}, for example, to avoid null values for primitive datatype wrappers.
     */
    protected T getNullValue()
    {
        return null;
    }

    @Override
    public abstract void fromBytes(ByteBuf buffer);

    @Override
    public abstract void toBytes(ByteBuf buffer);

    @Override
    public abstract void valueFromJSON(JsonElement element);

    @Override
    public abstract JsonElement valueToJSON();

    /**
     * set the value based on the specified tag
     * @param tag the tag should be the value without the necessity to search for a key
     */
    public abstract void valueFromNBT(NBTBase tag);

    /**
     * @return the value as instance of a subclass of {@link net.minecraft.nbt.NBTBase}.
     *         It can also return null, depending on the implementation in the subclasses.
     */
    @Nullable
    public abstract NBTBase valueToNBT();

    @Override
    public abstract GenericValue<T> clone();

    /**
     * Reset this value to defaultValue. If defaultValue inherits {@link mchorse.mclib.utils.ICloneable},
     * the defaultValue will be cloned so value and defaultValue don't share the same references.
     */
    @Override
    public void reset()
    {
        if (this.defaultValue instanceof ICloneable)
        {
            this.set(((ICloneable<T>) this.defaultValue).clone());
        }
        else
        {
            this.set(this.defaultValue);
        }
    }

    /**
     * Compare the objects based on their values. Ignores defaultValues etc.
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

    /**
     * @return true if value is null and defaultValue is not null or
     *         if the result of value.equals(defaultValue) is false.
     */
    public boolean hasChanged()
    {
        if (this.value == null && this.defaultValue == null)
        {
            return false;
        }
        else if (this.value == null)
        {
            return true;
        }

        return !this.value.equals(this.defaultValue);
    }
}
