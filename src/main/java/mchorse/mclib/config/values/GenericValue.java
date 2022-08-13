package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.ICopy;
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
 * If the generic datatype is a class, then that class should implement {@link ICopy}.
 * If the class of the generic datatype cannot implement {@link ICopy}, the methods {@link #set(Object)} and {@link #get()} need to be overridden!
 * <br>
 * The class of the generic datatype should also override {@link Object#equals(Object)} to ensure a safe usage.
 * If it cannot override {@link Object#equals(Object)}, the methods {@link #hasChanged()} and {@link #equals(Object)} need to be overridden!
 *
 * <br><br>
 * The GenericValue subclasses can be used together with the {@link mchorse.mclib.utils.ValueSerializer}
 * to automate all of the toNBT, fromNBT, toBytes, fromBytes, fromJson and toJson serialization processes.
 * GenericValue subclasses can also be used for an undo / redo system, as it is already in place in Aperture mod.
 *
 * @param <T> the datatype of the values in this value container
 * @author Christian F (Chryfi)
 */
public abstract class GenericValue<T> extends GenericBaseValue<T>
{
    protected T defaultValue;

    public GenericValue(String id)
    {
        super(id);
    }

    /**
     * Sets defaultValue to the provided defaultValue.
     * If defaultValue is null, the value of {@link #getNullValue()} will be set.
     * After defaultValue has been set the method {@link #reset()} will be called
     * @param id the id name of this value container
     * @param defaultValue
     */
    public GenericValue(String id, T defaultValue)
    {
        super(id);

        this.defaultValue = (defaultValue == null) ? this.getNullValue() : defaultValue;

        this.reset();
    }

    /**
     * Reset this value to defaultValue. Calls {@link #set(Object)}
     */
    @Override
    public void reset()
    {
        this.set(this.defaultValue);
    }

    /**
     * @return true if value is null and defaultValue is not null or
     *         if the result of value {@link Object#equals(Object)} is false.
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
