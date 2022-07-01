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

public class ValueSerializer implements IByteBufSerializable, INBTSerializable
{
    private final HashMap<String, GenericValue<?>> pool = new HashMap<>();
    private final HashMap<String, String> nbtMap = new HashMap<>();
    private final HashMap<String, String> jsonMap = new HashMap<>();

    public void registerNBTValue(String nbt, GenericValue<?> value)
    {
        this.registerValue(nbt, "", value);
    }

    public void registerJSONValue(String jsonMap, GenericValue<?> value)
    {
        this.registerValue("", jsonMap, value);
    }

    public void registerValue(String nbt, String jsonMap, GenericValue<?> value)
    {
        if (!nbt.isEmpty() && !this.nbtMap.containsKey(nbt))
        {
            this.nbtMap.put(nbt, this.poolValue(value));
        }

        if (!jsonMap.isEmpty() && !this.jsonMap.containsKey(jsonMap))
        {
            this.jsonMap.put(jsonMap, this.poolValue(value));
        }
    }

    /**
     * Only pool the value if the object is not pooled yet.
     * @param value
     * @return the new uuid string of the pooled value, or if the value already existed return the uuid of it.
     */
    protected String poolValue(GenericValue<?> value)
    {
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
