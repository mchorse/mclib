package mchorse.mclib.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.config.values.GenericValue;
import mchorse.mclib.network.IByteBufSerializable;
import mchorse.mclib.network.INBTSerializable;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A Serializer for GenericValue instances.
 * <br><br>
 * <h2>Ideal usage:</h2>
 * All the variables that should be serialized/deserialized need to be GenericValue subclasses.
 * Ideally, the references to these instances will not change throughout their lifetime, so the usage of final is advised.
 * In the constructor of the class with the variables, the references to all the GenericValue variables 
 * can now be registered to a ValueSerializer instance, 
 * using {@link #registerValue(String, String, GenericValue)} or {@link #registerJSONValue(String, GenericValue)} or {@link #registerNBTValue(String, GenericValue)}.
 */
public class ValueSerializer implements IByteBufSerializable, INBTSerializable
{
    /**
     * The key is a UUID String.
     * This HashMap will not include duplicate GenericValue references.
     */
    private final HashMap<String, GenericValue<?>> pool = new HashMap<>();

    /**
     * Key is the name that is used for serialization/deserialization
     * Value is the UUID String pointing to the key of {@link #pool}
     */
    private final HashMap<String, String> nbtMap = new HashMap<>();
    private final HashMap<String, String> jsonMap = new HashMap<>();

    /**
     * Register the provided GenericValue object reference
     * with the provided key name for NBT serialization/deserialization.
     * If the provided value is null or if the key already exists, it will not be registered.
     *
     * @param nbt name that should be used for serialization/deserialization
     *            If it is empty, the value will not be registered for NBT serialization/deserialization
     * @param value the reference to the value that should be serialization/deserialization
     */
    public void registerNBTValue(String nbt, GenericValue<?> value)
    {
        this.registerValue(nbt, "", value);
    }

    /**
     * Register the provided GenericValue object reference
     * with the provided key name for JSON serialization/deserialization.
     * If the provided value is null or if the key already exists, it will not be registered.
     *
     * @param json name that should be used for serialization/deserialization
     *                If it is empty, the value will not be registered for JSON serialization/deserialization
     * @param value the reference to the value that should be serialization/deserialization
     */
    public void registerJSONValue(String json, GenericValue<?> value)
    {
        this.registerValue("", json, value);
    }

    /**
     * Register the provided GenericValue object reference
     * with the provided key names for NBT and JSON serialization/deserialization.
     * If the provided value is null or if the key already exists, it will not be registered.
     *
     * @param nbt name that should be used for serialization and deserialization.
     *            If it is empty, the value will not be registered for NBT serialization/deserialization
     * @param json name that should be used for serialization/deserialization
     *                If it is empty, the value will not be registered for JSON serialization/deserialization
     * @param value the reference to the value that should be serialization/deserialization
     */
    public void registerValue(String nbt, String json, GenericValue<?> value)
    {
        if (value == null) return;

        if (!nbt.isEmpty() && !this.nbtMap.containsKey(nbt))
        {
            this.nbtMap.put(nbt, this.poolValue(value));
        }

        if (!json.isEmpty() && !this.jsonMap.containsKey(json))
        {
            this.jsonMap.put(json, this.poolValue(value));
        }
    }

    /**
     * Only pool the value if the object is not pooled yet. Null is not going to be added to the pool.
     * @param value
     * @return the new uuid string of the pooled value, or if the value already existed return the uuid of it.
     *         If the provided value was null, null will be returned.
     */
    protected String poolValue(GenericValue<?> value)
    {
        if (value == null) return null;

        String uuid = this.getValueReference(value);

        if (uuid == null)
        {
            uuid = UUID.randomUUID().toString();

            this.pool.put(uuid, value);
        }

        return uuid;
    }

    /**
     * This method searches for the same object reference in the pool of registered values
     * @param value
     * @return the UUID String if the reference to the specified value has been found.
     *         Returns null if the reference has not been found.
     */
    @Nullable
    protected String getValueReference(GenericValue<?> value)
    {
        if (value == null) return null;

        for (Map.Entry<String, GenericValue<?>> entry : this.pool.entrySet())
        {
            if (entry.getValue() == value)
            {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        for (GenericValue<?> value : this.pool.values())
        {
            value.fromBytes(buffer);
        }
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        for (GenericValue<?> value : this.pool.values())
        {
            value.toBytes(buffer);
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        for (Map.Entry<String, String> entry : this.nbtMap.entrySet())
        {
            GenericValue value = this.pool.get(entry.getValue());
            String key = entry.getKey();

            if (tag.hasKey(key))
            {
                value.valueFromNBT(tag.getTag(key));
            }
        }
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        for (Map.Entry<String, String> entry : this.nbtMap.entrySet())
        {
            GenericValue value = this.pool.get(entry.getValue());
            String key = entry.getKey();

            if (value.hasChanged() && value.get() != null && value.valueToNBT() != null)
            {
                tag.setTag(key, value.valueToNBT());
            }
        }

        return tag;
    }

    public JsonElement toJSON()
    {
        JsonObject jsonRoot = new JsonObject();

        for (Map.Entry<String, String> entry : this.jsonMap.entrySet())
        {
            GenericValue value = this.pool.get(entry.getValue());
            String key = entry.getKey();

            if (value.hasChanged() && value.get() != null)
            {
                jsonRoot.add(key, value.valueToJSON());
            }
        }

        return jsonRoot;
    }

    public void fromJSON(JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }

        JsonObject jsonObject = element.getAsJsonObject();

        for (Map.Entry<String, String> entry : this.jsonMap.entrySet())
        {
            GenericValue value = this.pool.get(entry.getValue());
            String key = entry.getKey();

            if (jsonObject.has(key))
            {
                value.fromJSON(jsonObject.get(key));
            }
        }
    }
}
