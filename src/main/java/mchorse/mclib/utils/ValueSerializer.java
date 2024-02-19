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

import java.util.*;

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
public class ValueSerializer implements IByteBufSerializable, INBTSerializable, ICopy<ValueSerializer>
{
    /**
     * A pool of values, so we can easily retrieve a value that has been either registered for json or nbt.
     * The key is the path of the value.
     */
    private final Map<String, Value<?>> pool = new LinkedHashMap<>();

    /**
     * Key is the name that is used for serialization/deserialization
     * Value is the value's path -> Key in {@link #pool}.
     */
    private final Map<String, String> nbtMap = new HashMap<>();
    /**
     * Key is the name that is used for serialization/deserialization
     * Value is the value's path -> Key in {@link #pool}.
     */
    private final Map<String, String> jsonMap = new HashMap<>();

    /**
     * Register a value.
     * @param value
     * @returns a helper object where you can configure serialization.
     * @throws IllegalArgumentException when the path of the value is already registered with a different value.
     */
    public <T> Value<T> registerValue(GenericBaseValue<T> value)
    {
        return this.poolValue(value);
    }

    public Optional<GenericBaseValue<?>> getValue(String path)
    {
        return this.pool.containsKey(path) ? Optional.of(this.pool.get(path).value) : Optional.empty();
    }

    /**
     * @return a list of all the registered values.
     */
    public List<GenericBaseValue<?>> getValues()
    {
        List<GenericBaseValue<?>> values = new ArrayList<>();
        for (Value<?> value : this.pool.values())
        {
            values.add(value.value);
        }

        return values;
    }

    /**
     * Register the provided GenericValue object reference
     * with the provided key name for NBT serialization/deserialization.
     * If the provided value is null or if the key already exists, it will not be registered.
     *
     * @param nbt name that should be used for serialization/deserialization.
     *            If it is empty, the value will not be registered for NBT serialization/deserialization
     * @param value the reference to the value that should be serialization/deserialization
     * @throws IllegalArgumentException when the path of the value is already registered.
     * @deprecated use {@link #registerValue(GenericBaseValue)} and the methods of {@link Value}
     */
    @Deprecated
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
     * @throws IllegalArgumentException when the path of the value is already registered.
     * @deprecated use {@link #registerValue(GenericBaseValue)} and the methods of {@link Value}
     */
    @Deprecated
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
     * @throws IllegalArgumentException when the path of the value is already registered.
     * @deprecated use {@link #registerValue(GenericBaseValue)} and the methods of {@link Value}
     */
    @Deprecated
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
     * @throws IllegalArgumentException when the path of the value is already registered.
     * @deprecated use {@link #registerValue(GenericBaseValue)} and the methods of {@link Value}
     */
    @Deprecated
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
     * @throws IllegalArgumentException when the path of the value is already registered.
     * @deprecated use {@link #registerValue(GenericBaseValue)} and the methods of {@link Value}
     */
    @Deprecated
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
     * @throws IllegalArgumentException when the path of the value is already registered.
     * @deprecated use {@link #registerValue(GenericBaseValue)} and the methods of {@link Value}
     */
    @Deprecated
    public void registerValue(String nbt, String json, GenericBaseValue<?> value, boolean alwaysWriteNBT, boolean alwaysWriteJSON)
    {
        if (!nbt.isEmpty() && !this.nbtMap.containsKey(nbt))
        {
            Value<?> packet = this.poolValue(value);
            packet.serializeNBT(nbt, alwaysWriteNBT);
        }

        if (!json.isEmpty() && !this.jsonMap.containsKey(json))
        {
            Value<?> packet = this.poolValue(value);
            packet.serializeJSON(json, alwaysWriteJSON);
        }
    }

    /**
     * @throws IllegalArgumentException when the path of the value is already registered.
     */
    protected <T> Value<T> poolValue(GenericBaseValue<T> value)
    {
        String key = value.getPath();
        if (this.pool.containsKey(key) && this.pool.get(key).value != value)
        {
            throw new IllegalArgumentException("The provided value's path is already registered with a different value reference.");
        }

        Value<T> packet;
        if (this.pool.containsKey(key))
        {
            packet = (Value<T>) this.pool.get(key);
        }
        else
        {
            packet = new Value<>(value, this);
            this.pool.put(key, packet);
        }

        return packet;
    }

    /**
     * Registers the NBT value for serialization for quick access.
     * Has some safety checks to ensure that pooled values and serialization maps are consistent.
     * @throws IllegalArgumentException when the name is already registered with a different value reference.
     */
    protected void putNBTValue(String name, Value<?> value)
    {
        String key = value.value.getPath();
        if (this.nbtMap.containsKey(name) && !this.nbtMap.get(name).equals(key))
        {
            throw new IllegalArgumentException("The provided nbt name is already registered with a different value reference.");
        }

        if (this.pool.containsKey(key) && this.pool.get(key).value != value.value)
        {
            throw new IllegalArgumentException("The provided value's path is already registered with a different value reference.");
        }

        this.pool.put(key, value);
        this.nbtMap.put(name, key);
    }

    /**
     * @throws IllegalArgumentException when the name is already registered with a different value reference.
     */
    protected void putJSONValue(String name, Value<?> value)
    {
        String key = value.value.getPath();
        if (this.jsonMap.containsKey(name) && !this.jsonMap.get(name).equals(key))
        {
            throw new IllegalArgumentException("The provided json name is already registered with a different value reference.");
        }

        if (this.pool.containsKey(key) && this.pool.get(key).value != value.value)
        {
            throw new IllegalArgumentException("The provided value's path is already registered with a different value reference.");
        }

        this.pool.put(key, value);
        this.jsonMap.put(name, key);
    }

    /**
     * Calls {@link GenericBaseValue#valueFromBytes(ByteBuf)}
     */
    @Override
    public void fromBytes(ByteBuf buffer)
    {
        for (Value<?> value : this.pool.values())
        {
            value.value.valueFromBytes(buffer);
        }
    }

    /**
     * Calls {@link GenericBaseValue#valueToBytes(ByteBuf)}
     */
    @Override
    public void toBytes(ByteBuf buffer)
    {
        for (Value<?> value : this.pool.values())
        {
            value.value.valueToBytes(buffer);
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
            Value<?> packet = this.pool.get(entry.getValue());
            GenericBaseValue<?> value = packet.value;
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
            Value<?> packet = this.pool.get(entry.getValue());
            GenericBaseValue<?> value = packet.value;

            if (!packet.nbtAlwaysWrite && value instanceof GenericValue && !((GenericValue) value).hasChanged())
            {
                continue;
            }

            tag.setTag(entry.getKey(), value.valueToNBT());
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
            Value<?> packet = this.pool.get(entry.getValue());
            GenericBaseValue<?> value = packet.value;
            if (!packet.jsonAlwaysWrite && value instanceof GenericValue && !((GenericValue) value).hasChanged())
            {
                continue;
            }

            jsonRoot.add(entry.getKey(), value.valueToJSON());
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
            Value<?> packet = this.pool.get(entry.getValue());
            GenericBaseValue<?> value = packet.value;
            String key = entry.getKey();

            if (jsonObject.has(key))
            {
                value.valueFromJSON(jsonObject.get(key));
            }
        }
    }

    /**
     * Copies the values of the origin with matching paths to the values in this.
     * Only copies if copyable is true for the respective value.
     * @param origin
     */
    public void copyValues(ValueSerializer origin)
    {
        for (Map.Entry<String, Value<?>> originEntry : origin.pool.entrySet())
        {
            if (!originEntry.getValue().copyable) continue;

            this.pool.get(originEntry.getKey()).value.copy(originEntry.getValue().value);
        }
    }

    /**
     * Compare the values with matching paths using the {@link GenericBaseValue#equals(Object)} method.
     * @param serializer
     * @return true if all values with matching paths are equal.
     */
    public boolean equalsValues(ValueSerializer serializer)
    {
        for (Map.Entry<String, Value<?>> entryOrigin : serializer.pool.entrySet())
        {
            String path = entryOrigin.getKey();
            GenericBaseValue<?> originValue = entryOrigin.getValue().value;

            if (this.pool.containsKey(path) && !this.pool.get(path).value.equals(originValue)) return false;
        }

        return true;
    }

    /**
     * @return deep copy of this valueSerializer
     */
    @Override
    public ValueSerializer copy()
    {
        ValueSerializer copy = new ValueSerializer();
        copy.copy(this);
        return copy;
    }

    @Override
    public void copy(ValueSerializer origin)
    {
        this.jsonMap.clear();
        this.jsonMap.putAll(origin.jsonMap);

        this.nbtMap.clear();
        this.nbtMap.putAll(origin.nbtMap);

        for (Map.Entry<String, Value<?>> entryOrigin : origin.pool.entrySet())
        {
            this.pool.put(entryOrigin.getKey(), entryOrigin.getValue().copy(this));
        }
    }

    public static class Value<T>
    {
        private String nbt;
        private boolean nbtAlwaysWrite;
        private String json;
        private boolean jsonAlwaysWrite;
        private boolean copyable = true;
        private GenericBaseValue<T> value;
        private ValueSerializer serializer;

        public Value(GenericBaseValue<T> value, ValueSerializer serializer)
        {
            this.value = value;
            this.serializer = serializer;
        }

        /**
         * @throws IllegalArgumentException when the name is already registered with a different value reference.
         */
        public Value<T> serializeNBT(String nbt)
        {
            return this.serializeNBT(nbt, false);
        }

        /**
         * @throws IllegalArgumentException when the name is already registered with a different value reference.
         */
        public Value<T> serializeNBT(String nbt, boolean alwaysWrite)
        {
            this.nbt = nbt;
            this.nbtAlwaysWrite = alwaysWrite;
            this.serializer.putNBTValue(nbt, this);
            return this;
        }

        /**
         * @throws IllegalArgumentException when the name is already registered with a different value reference.
         */
        public Value<T> serializeJSON(String json)
        {
            return this.serializeJSON(json, false);
        }

        /**
         * @throws IllegalArgumentException when the name is already registered with a different value reference.
         */
        public Value<T> serializeJSON(String json, boolean alwaysWrite)
        {
            this.json = json;
            this.jsonAlwaysWrite = alwaysWrite;
            this.serializer.putJSONValue(json, this);
            return this;
        }

        public Value<T> copyable()
        {
            this.copyable = true;
            return this;
        }

        public Value<T> nonCopyable()
        {
            this.copyable = false;
            return this;
        }

        public Value<T> copy(ValueSerializer destinationSerializer) {
            Value<T> copy = new Value<>(this.value.copy(), destinationSerializer);
            copy.json = this.json;
            copy.jsonAlwaysWrite = this.jsonAlwaysWrite;
            copy.nbt = this.nbt;
            copy.nbtAlwaysWrite = this.nbtAlwaysWrite;
            copy.copyable = this.copyable;
            return copy;
        }
    }
}
