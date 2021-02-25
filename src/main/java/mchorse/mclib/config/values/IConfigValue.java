package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import mchorse.mclib.network.IByteBufSerializable;

import java.util.List;

public interface IConfigValue extends IByteBufSerializable
{
    public String getId();

    public List<IConfigValue> getSubValues();

    public Object getValue();

    public void setValue(Object value);

    public void reset();

    public boolean isVisible();

    public boolean isClientSide();

    public boolean isSyncable();

    public void copy(IConfigValue value);

    public void fromJSON(JsonElement element);

    public JsonElement toJSON();
}