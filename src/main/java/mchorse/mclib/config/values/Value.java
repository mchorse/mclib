package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigManager;
import mchorse.mclib.network.IByteBufSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Value implements IByteBufSerializable
{
    public final String id;
    private boolean visible = true;
    private boolean clientSide;
    private boolean syncable;

    private Config config;
    private Map<String, Value> children = new LinkedHashMap<String, Value>();
    private Value parent;

    public Value(String id)
    {
        this.id = id;
    }

    public Object getValue()
    {
        return null;
    }

    public void setValue(Object value)
    {}

    public void reset()
    {}

    /* Hierarchy code */

    public Config getConfig()
    {
        return this.config;
    }

    public void setConfig(Config config)
    {
        this.config = config;
    }

    public void removeAllSubValues()
    {
        this.children.clear();
    }

    public List<Value> getSubValues()
    {
        return new ArrayList<Value>(this.children.values());
    }

    public void addSubValue(Value value)
    {
        if (value != null)
        {
            this.children.put(value.id, value);
            value.parent = this;
        }
    }

    public Value getSubValue(String key)
    {
        return this.children.get(key);
    }

    public void removeSubValue(String key)
    {
        this.children.remove(key);
    }

    public Value getRoot()
    {
        Value value = this;

        while (value != null)
        {
            if (value.parent == null)
            {
                return value;
            }

            value = value.parent;
        }

        return null;
    }

    public Value getParent()
    {
        return this.parent;
    }

    public Value setParent(Value parent)
    {
        this.parent = parent;

        return this;
    }

    public String getPath()
    {
        List<String> strings = new ArrayList<String>();
        Value value = this;

        while (value != null)
        {
            if (!value.id.isEmpty())
            {
                strings.add(value.id);
            }

            value = value.parent;
        }

        Collections.reverse(strings);

        return String.join(".", strings);
    }

    /* Base property getters and setters */

    public Value invisible()
    {
        this.visible = false;

        return this;
    }

    public Value clientSide()
    {
        this.clientSide = true;

        return this;
    }

    public Value markClientSide()
    {
        for (Value value : this.children.values())
        {
            value.markClientSide();
        }

        return this.clientSide();
    }

    public Value syncable()
    {
        this.syncable = true;

        return this;
    }

    public boolean isVisible()
    {
        boolean visible = true;
        Value value = this;

        while (value != null)
        {
            visible = visible && value.visible;
            value = value.parent;
        }

        return visible;
    }

    public boolean isClientSide()
    {
        boolean visible = false;
        Value value = this;

        while (value != null)
        {
            visible = visible || value.clientSide;
            value = value.parent;
        }

        return visible;
    }

    public boolean isSyncable()
    {
        return this.syncable;
    }

    public boolean hasSyncable()
    {
        if (this.syncable)
        {
            return true;
        }

        for (Value value : this.children.values())
        {
            if (value.hasSyncable())
            {
                return true;
            }
        }

        return false;
    }

    public void saveLater()
    {
        if (this.config != null)
        {
            this.config.saveLater();
        }
    }

    /* Saving data */

    public void fromJSON(JsonElement element)
    {
        if (element.isJsonObject())
        {
            JsonObject object = element.getAsJsonObject();

            if (object.has("value") && object.has("subvalues") && object.size() == 2)
            {
                this.childrenFromJSON(object.get("subvalues").getAsJsonObject());
                this.valueFromJSON(object.get("value"));
            }
            else
            {
                this.childrenFromJSON(object);
            }
        }
        else
        {
            this.valueFromJSON(element);
        }
    }

    private void childrenFromJSON(JsonObject object)
    {
        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            Value value = this.children.get(entry.getKey());

            if (value != null)
            {
                value.reset();
                value.setParent(this);
                value.fromJSON(entry.getValue());
            }
        }
    }

    protected void valueFromJSON(JsonElement element)
    {}

    public JsonElement toJSON()
    {
        JsonElement child = this.valueToJSON();
        JsonObject object = new JsonObject();

        for (Value value : this.children.values())
        {
            object.add(value.id, value.toJSON());
        }

        if (child.isJsonNull())
        {
            return object;
        }
        else if (object.size() == 0)
        {
            return child;
        }

        JsonObject container = new JsonObject();

        container.add("value", child);
        container.add("subvalues", object);

        return container;
    }

    protected JsonElement valueToJSON()
    {
        return JsonNull.INSTANCE;
    }

    public void copy(Value category)
    {
        for (Map.Entry<String, Value> entry : category.children.entrySet())
        {
            this.children.get(entry.getKey()).copy(entry.getValue());
        }
    }

    public void copyServer(Value category)
    {
        for (Map.Entry<String, Value> entry : category.children.entrySet())
        {
            Value value = this.children.get(entry.getKey());

            if (value != null && value.isSyncable() && value instanceof IServerValue)
            {
                ((IServerValue) value).copyServer(entry.getValue());
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.superFromBytes(buffer);
    }

    /**
     * For internal usage in subclasses of {@link GenericValue}.
     * @param buffer
     */
    protected final void superFromBytes(ByteBuf buffer)
    {
        this.visible = buffer.readBoolean();
        this.clientSide = buffer.readBoolean();

        this.children.clear();

        for (int i = 0, c = buffer.readInt(); i < c; i++)
        {
            Value value = ConfigManager.fromBytes(buffer);

            if (value != null)
            {
                value.setConfig(this.config);
                value.setParent(this);
                this.addSubValue(value);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        this.superToBytes(buffer);
    }

    /**
     * For internal usage in subclasses of {@link GenericValue}.
     * @param buffer
     */
    protected final void superToBytes(ByteBuf buffer)
    {
        buffer.writeBoolean(this.visible);
        buffer.writeBoolean(this.clientSide);

        buffer.writeInt(this.children.size());

        for (Map.Entry<String, Value> entry : this.children.entrySet())
        {
            ConfigManager.toBytes(buffer, entry.getValue());
        }
    }

    public void resetServerValues()
    {
        for (Value value : this.children.values())
        {
            if (value instanceof IServerValue)
            {
                ((IServerValue) value).resetServer();
            }
        }
    }

    /* Client side stuff */

    @SideOnly(Side.CLIENT)
    public String getLabelKey()
    {
        return this.config.getValueLabelKey(this);
    }

    @SideOnly(Side.CLIENT)
    public String getCommentKey()
    {
        return this.config.getValueCommentKey(this);
    }
}