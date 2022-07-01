package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.ICloneable;
import mchorse.mclib.utils.ValueSerializer;
import net.minecraft.nbt.NBTBase;

import javax.annotation.Nullable;

/**
 * A Value container, providing an interface for dealing with
 * the {@link #value}, {@link #defaultValue} and {@link #serverValue}.
 * Using the {@link #serverValue}, it can be used for server side syncing, for example, in configs.
 * If a subclass extending this should be used in configs with server side syncing visit {@link IServerValue}.
 *
 * <br><br>
 *
 * <h2>Important when extending:</h2>
 * When extending from GenericValue and if the generic datatype is a class,
 * then the class should implement {@link ICloneable}.
 * The class should also override {@link #equals(Object)} to ensure a safe usage with this value container.
 *
 * <br><br>
 * The GenericValue subclasses can be used together with the {@link mchorse.mclib.utils.ValueSerializer}
 * to automate all of the toNBT, fromNBT, toBytes, fromBytes, fromJson and toJson serialization processes.
 * GenericValue subclasses can also be used for an undo / redo system, as it is already in place in Aperture mod.
 *
 * @param <T> the datatype of the values in this value container
 * @author Christian F (Chryfi)
 */
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
     * Compare the objects based on their {@link #value} variables. Ignores the other variables.
     * @param obj
     * @return true if this object's {@link #value} equals the specified object's {@link #value}, using {@link #equals(Object)}.
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
