package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiKeybindElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.keys.KeyParser;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValueInt extends Value implements IServerValue, IConfigGuiProvider
{
    private int value;
    private int defaultValue;
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;
    private Subtype subtype = Subtype.INTEGER;
    private List<IKey> labels;

    private Integer serverValue;

    public ValueInt(String id)
    {
        super(id);
    }

    public ValueInt(String id, int defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;

        this.reset();
    }

    public ValueInt(String id, int defaultValue, int min, int max)
    {
        super(id);

        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;

        this.reset();
    }

    public int getMin()
    {
        return this.min;
    }

    public int getMax()
    {
        return this.max;
    }

    public int get()
    {
        return this.serverValue == null ? this.value : this.serverValue;
    }

    public void set(int value)
    {
        this.value = MathUtils.clamp(value, this.min, this.max);
        this.saveLater();
    }

    @Override
    public Object getValue()
    {
        return this.get();
    }

    @Override
    public void setValue(Object value)
    {
        if (value instanceof Integer)
        {
            this.set((Integer) value);
        }
    }

    public void setColorValue(String value)
    {
        this.set(ColorUtils.parseColor(value));
    }

    public Subtype getSubtype()
    {
        return this.subtype;
    }

    public ValueInt subtype(Subtype subtype)
    {
        this.subtype = subtype;

        return this;
    }

    public ValueInt color()
    {
        return this.subtype(Subtype.COLOR);
    }

    public ValueInt colorAlpha()
    {
        return this.subtype(Subtype.COLOR_ALPHA);
    }

    public ValueInt keybind()
    {
        return this.subtype(Subtype.KEYBIND);
    }

    public ValueInt modes(IKey... labels)
    {
        this.labels = new ArrayList<IKey>();
        Collections.addAll(this.labels, labels);

        return this.subtype(Subtype.MODES);
    }

    @Override
    public void reset()
    {
        this.set(this.defaultValue);
    }

    @Override
    public void resetServer()
    {
        this.serverValue = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiElement element = new GuiElement(mc);
        GuiLabel label = Elements.label(IKey.lang(this.getLabelKey()), 0).anchor(0, 0.5F);

        element.flex().row(0).preferred(0).height(20);
        element.add(label);

        if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
        {
            GuiColorElement color = new GuiColorElement(mc, this);

            color.flex().w(90);
            element.add(color.removeTooltip());
        }
        else if (this.subtype == Subtype.KEYBIND)
        {
            GuiKeybindElement keybind = new GuiKeybindElement(mc, this);

            keybind.flex().w(90);
            element.add(keybind.removeTooltip());
        }
        else if (this.subtype == Subtype.MODES)
        {
            GuiCirculateElement button = new GuiCirculateElement(mc, null);

            for (IKey key : this.labels)
            {
                button.addLabel(key);
            }

            button.callback = (b) -> this.set(button.getValue());
            button.setValue(this.get());
            button.flex().w(90);
            element.add(button);
        }
        else
        {
            GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);

            trackpad.flex().w(90);
            element.add(trackpad.removeTooltip());
        }

        return Arrays.asList(element.tooltip(IKey.lang(this.getCommentKey())));
    }

    @Override
    public void valueFromJSON(JsonElement element)
    {
        this.set(element.getAsInt());
    }

    @Override
    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value);
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        try
        {
            if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
            {
                this.set(ColorUtils.parseColorWithException(value));
            }
            else
            {
                this.set(Integer.parseInt(value));
            }

            return true;
        }
        catch (Exception e)
        {}

        return false;
    }

    @Override
    public void copy(Value value)
    {
        if (value instanceof ValueInt)
        {
            this.value = ((ValueInt) value).value;
        }
    }

    @Override
    public void copyServer(Value value)
    {
        if (value instanceof ValueInt)
        {
            this.serverValue = ((ValueInt) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);

        this.value = buffer.readInt();
        this.defaultValue = buffer.readInt();
        this.min = buffer.readInt();
        this.max = buffer.readInt();

        this.subtype = Subtype.values()[buffer.readInt()];

        if (buffer.readBoolean())
        {
            this.labels = new ArrayList<IKey>();

            for (int i = 0, c = buffer.readInt(); i < c; i++)
            {
                IKey key = KeyParser.keyFromBytes(buffer);

                if (key != null)
                {
                    this.labels.add(key);
                }
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);

        buffer.writeInt(this.value);
        buffer.writeInt(this.defaultValue);
        buffer.writeInt(this.min);
        buffer.writeInt(this.max);

        buffer.writeInt(this.subtype.ordinal());
        buffer.writeBoolean(this.labels != null);

        if (this.labels != null)
        {
            buffer.writeInt(this.labels.size());

            for (IKey key : this.labels)
            {
                KeyParser.keyToBytes(buffer, key);
            }
        }
    }

    @Override
    public String toString()
    {
        if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
        {
            return "#" + Integer.toHexString(this.value);
        }

        return Integer.toString(this.value);
    }

    public static enum Subtype
    {
        INTEGER,
        COLOR,
        COLOR_ALPHA,
        KEYBIND,
        MODES
    }
}