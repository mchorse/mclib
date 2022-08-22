package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.ICopy;
import net.minecraft.nbt.NBTBase;

import javax.annotation.Nullable;

/**
 * A Value container, providing an interface for dealing with
 * the {@link #value} and {@link #serverValue}. The subclass {@link GenericValue} has a defaultValue.
 * Using the {@link #serverValue}, it can be used for server side syncing, for example, in configs.
 * If a subclass extending this should be used in configs with server side syncing visit {@link IServerValue}.
 *
 * <br><br>
 *
 * <h2>Important when extending:</h2>
 * If the generic datatype is a class, then that class should implement {@link ICopy}.
 * If the class of the generic datatype cannot implement {@link ICopy}, the methods {@link #set(Object)} and {@link #get()} need to be overridden!
 * <br>
 * The class of the generic datatype should also override {@link Object#equals(Object)} to ensure a safe usage.
 * If it cannot override {@link Object#equals(Object)}, the method {@link #equals(Object)} need to be overridden!
 *
 * <br><br>
 * The GenericBaseValue subclasses can be used together with the {@link mchorse.mclib.utils.ValueSerializer}
 * to automate all of the toNBT, fromNBT, toBytes, fromBytes, fromJson and toJson serialization processes.
 * GenericBaseValue subclasses can also be used for an undo / redo system, as it is already in place in Aperture mod.
 *
 * @param <T> the datatype of the values in this value container
 * @author Christian F (Chryfi)
 */
public abstract class GenericBaseValue<T> extends Value
{
    protected T value;
    protected T serverValue;

    public GenericBaseValue(String id)
    {
        super(id);
    }

    public GenericBaseValue(String id, T value)
    {
        super(id);

        this.value = value;
    }

    /**
     * @return reference to {@link #value}, or if the {@link #serverValue} != null return {@link #serverValue}.
     *         If the generic datatype is instance of {@link ICopy}, the return value will be copied.
     */
    public T get()
    {
        if (this.serverValue == null)
        {
            if (this.value instanceof ICopy)
            {
                return ((ICopy<T>) this.value).copy();
            }
            else
            {
                return this.value;
            }
        }
        else
        {
            if (this.serverValue instanceof ICopy)
            {
                return ((ICopy<T>) this.serverValue).copy();
            }
            else
            {
                return this.serverValue;
            }
        }
    }


    /**
     * Set the value of this instance to the provided value. If the value is instanceOf {@link ICopy}, it will be copied.
     * If the value is null, the result of {@link #getNullValue()} will be assigned.
     * This method calls {@link #saveLater()}
     * @param value
     */
    public void set(T value)
    {
        if (value == null)
        {
            this.value = this.getNullValue();
        }
        else
        {
            if (value instanceof ICopy)
            {
                this.value = ((ICopy<T>) value).copy();
            }
            else
            {
                this.value = value;
            }
        }

        this.saveLater();
    }

    /**
     * @return the default value that this type produces when not being initialised.
     *         This is used in {@link #set(T)}, for example, to avoid null values for primitive datatype wrappers.
     */
    protected T getNullValue()
    {
        return null;
    }


    /**
     * Only used in Aperture undo/redo system
     * @param value if the specified object is not null and assignable to the generic type T,
     *             the value be set, using the {@link #set(Object)} method.
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
     * @return a deep copy of this object
     */
    @Override
    public abstract GenericBaseValue<T> copy();

    /**
     * Copy the {@link #value} from the specified object to this object.
     * @param origin the origin that should be copied from
     */
    @Override
    public abstract void copy(Value origin);

    /**
     * Compare the objects based on their {@link #value} variables. Ignores the other variables.
     * @param obj
     * @return true if this object's {@link #value} equals the specified object's {@link #value}, using {@link #equals(Object)}.
     *         Or if the specified object or its value and this.value are both null.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof GenericBaseValue))
        {
            /*
             * if this.value == null and obj is null return true because both values are null
             */
            return (obj == null && this.value == null);
        }

        GenericBaseValue valueObj = (GenericBaseValue) obj;

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

        return this == obj;
    }

    /**
     * Read all contents from this object from the ByteBuffer and call {@link #superFromBytes(ByteBuf)}
     * @param buffer
     */
    @Override
    public abstract void fromBytes(ByteBuf buffer);

    /**
     * Write all contents from this object to the ByteBuffer and call {@link #superToBytes(ByteBuf)}
     * @param buffer
     */
    @Override
    public abstract void toBytes(ByteBuf buffer);

    /**
     * Only read the {@link #value} from the ByteBuffer
     * @param buffer
     */
    public abstract void valueFromBytes(ByteBuf buffer);

    /**
     * Only write the {@link #value} into the ByteBuffer
     * @param buffer
     */
    public abstract void valueToBytes(ByteBuf buffer);

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
}
