package mchorse.mclib.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.config.values.GenericBaseValue;
import mchorse.mclib.config.values.GenericValue;
import mchorse.mclib.network.IByteBufSerializable;
import mchorse.mclib.network.INBTSerializable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
 * using {@link #registerValue(String, String, GenericBaseValue)} or {@link #registerJSONValue(String, GenericBaseValue)} or {@link #registerNBTValue(String, GenericBaseValue)}.
 *
 * @author Christian F. (Chryfi)
 */
public class ValueSerializer implements IByteBufSerializable, INBTSerializable
{
    /**
     * The key is a UUID String.
     * This HashMap will not include duplicate GenericValue references.
     */
    private final Map<String, GenericBaseValue<?>> pool = new LinkedHashMap<>();

    /**
     * Key is the name that is used for serialization/deserialization
     * Value is the UUID String pointing to the key of {@link #pool}
     */
    private final Map<String, String> nbtMap = new HashMap<>();
    /**
     * Contains the UUID to a value if it should always be written, no matter if it changed or not
     */
    private final List<String> nbtAlwaysWrite = new ArrayList<>();

    private final Map<String, String> jsonMap = new HashMap<>();
    /**
     * Contains the UUID to a value if it should always be written, no matter if it changed or not
     */
    private final List<String> jsonAlwaysWrite = new ArrayList<>();

    /**
     * Register the provided GenericValue object reference
     * with the provided key name for NBT serialization/deserialization.
     * If the provided value is null or if the key already exists, it will not be registered.
     *
     * @param nbt name that should be used for serialization/deserialization.
     *            If it is empty, the value will not be registered for NBT serialization/deserialization
     * @param value the reference to the value that should be serialization/deserialization
     */
    public void registerNBTValue(String nbt, GenericBaseValue<?> value)
    {
        this.registerValue(nbt, "", value, false, false);
    }

    /**
     * Register the provided GenericValue object reference
     * with the provided key name for NBT serialization/deserialization.
     * If the provided value is null or if the key already exists, it will not be registered.
     *
     * @param nbt name that should be used for serialization/deserialization.
     *            If it is empty, the value will not be registered for NBT serialization/deserialization
     * @param value the reference to the value that should be serialization/deserialization
     * @param alwaysWrite when true, the value will always be serialized to NBT, no matter if it changed or not.
     */
    public void registerNBTValue(String nbt, GenericBaseValue<?> value, boolean alwaysWrite)
    {
        this.registerValue(nbt, "", value, alwaysWrite, false);
    }

    /**
     * Register the provided GenericValue object reference
     * with the provided key name for JSON serialization/deserialization.
     * If the provided value is null or if the key already exists, it will not be registered.
     *
     * @param json name that should be used for serialization/deserialization.
     *                If it is empty, the value will not be registered for JSON serialization/deserialization
     * @param value the reference to the value that should be serialization/deserialization
     */
    public void registerJSONValue(String json, GenericBaseValue<?> value)
    {
        this.registerValue("", json, value, false, false);
    }

    /**
     * Register the provided GenericValue object reference
     * with the provided key name for JSON serialization/deserialization.
     * If the provided value is null or if the key already exists, it will not be registered.
     *
     * @param json name that should be used for serialization/deserialization.
     *                If it is empty, the value will not be registered for JSON serialization/deserialization
     * @param value the reference to the value that should be serialization/deserialization
     * @param alwaysWrite when true, the value will always be serialized to JSON, no matter if it changed or not.
     */
    public void registerJSONValue(String json, GenericBaseValue<?> value, boolean alwaysWrite)
    {
        this.registerValue("", json, value, false, alwaysWrite);
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
    public void registerValue(String nbt, String json, GenericBaseValue<?> value)
    {
        this.registerValue(nbt, json, value, false, false);
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
     * @param alwaysWriteJSON when true, the value will always be serialized to JSON, no matter if it changed or not.
     * @param alwaysWriteNBT when true, the value will always be serialized to NBT, no matter if it changed or not.
     */
    public void registerValue(String nbt, String json, GenericBaseValue<?> value, boolean alwaysWriteNBT, boolean alwaysWriteJSON)
    {
        if (value == null) return;

        if (!nbt.isEmpty() && !this.nbtMap.containsKey(nbt))
        {
            String uuid = this.poolValue(value);

            this.nbtMap.put(nbt, uuid);

            if (alwaysWriteNBT) this.nbtAlwaysWrite.add(uuid);
        }

        if (!json.isEmpty() && !this.jsonMap.containsKey(json))
        {
            String uuid = this.poolValue(value);

            this.jsonMap.put(json, uuid);

            if (alwaysWriteJSON) this.jsonAlwaysWrite.add(uuid);
        }
    }

    /**
     * Only pool the value if the object is not pooled yet. Null is not going to be added to the pool.
     * @param value
     * @return the new uuid string of the pooled value, or if the value already existed return the uuid of it.
     *         If the provided value was null, null will be returned.
     */
    protected String poolValue(GenericBaseValue<?> value)
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
    protected String getValueReference(GenericBaseValue<?> value)
    {
        if (value == null) return null;

        for (Map.Entry<String, GenericBaseValue<?>> entry : this.pool.entrySet())
        {
            if (entry.getValue() == value)
            {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Calls {@link GenericBaseValue#valueFromBytes(ByteBuf)}
     */
    @Override
    public void fromBytes(ByteBuf buffer)
    {
        for (GenericBaseValue<?> value : this.pool.values())
        {
            value.valueFromBytes(buffer);
        }
    }

    /**
     * Calls {@link GenericBaseValue#valueToBytes(ByteBuf)}
     */
    @Override
    public void toBytes(ByteBuf buffer)
    {
        for (GenericBaseValue<?> value : this.pool.values())
        {
            value.valueToBytes(buffer);
        }
    }

    /**
     * Calls {@link GenericBaseValue#valueFromNBT(NBTBase)}
     */
    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        for (Map.Entry<String, String> entry : this.nbtMap.entrySet())
        {
            GenericBaseValue value = this.pool.get(entry.getValue());
            String key = entry.getKey();

            if (tag.hasKey(key))
            {
                value.valueFromNBT(tag.getTag(key));
            }
        }
    }

    /**
     * Calls {@link GenericBaseValue#valueToNBT()}. Only serializes the value if it has changed.
     * Serializes always, if the value has been registered with alwaysWrite flag true.
     */
    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        for (Map.Entry<String, String> entry : this.nbtMap.entrySet())
        {
            GenericBaseValue value = this.pool.get(entry.getValue());
            String uuid = entry.getKey();

            if (!this.nbtAlwaysWrite.contains(uuid) && value instanceof GenericValue && !((GenericValue) value).hasChanged())
            {
                continue;
            }

            tag.setTag(uuid, value.valueToNBT());
        }

        return tag;
    }

    /**
     * Calls {@link GenericBaseValue#valueToJSON()}. Only serialize the value if it has changed.
     * Serializes always, if the value has been registered with alwaysWrite flag true.
     */
    public JsonElement toJSON()
    {
        JsonObject jsonRoot = new JsonObject();

        for (Map.Entry<String, String> entry : this.jsonMap.entrySet())
        {
            GenericBaseValue value = this.pool.get(entry.getValue());
            String uuid = entry.getKey();

            if (!this.jsonAlwaysWrite.contains(uuid) && value instanceof GenericValue && !((GenericValue) value).hasChanged())
            {
                continue;
            }

            jsonRoot.add(uuid, value.valueToJSON());
        }

        return jsonRoot;
    }

    /**
     * Calls {@link GenericBaseValue#valueFromJSON(JsonElement)}
     * @param element
     */
    public void fromJSON(JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }

        JsonObject jsonObject = element.getAsJsonObject();

        for (Map.Entry<String, String> entry : this.jsonMap.entrySet())
        {
            GenericBaseValue value = this.pool.get(entry.getValue());
            String key = entry.getKey();

            if (jsonObject.has(key))
            {
                value.valueFromJSON(jsonObject.get(key));
            }
        }
    }
}
