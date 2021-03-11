package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfigPanel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueString extends Value implements IServerValue, IConfigGuiProvider
{
    private String value = "";
    private String defaultValue = "";

    private String serverValue;

    public ValueString(String id)
    {
        super(id);
    }

    public ValueString(String id, String defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;

        this.reset();
    }

    public String get()
    {
        return this.serverValue == null ? this.value : this.serverValue;
    }

    public void set(String value)
    {
        this.value = value;
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
        if (value instanceof String)
        {
            this.set((String) value);
        }
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
        GuiLabel label = Elements.label(IKey.lang(this.getConfig().getValueLabelKey(this)), 0).anchor(0, 0.5F);
        GuiTextElement textbox = new GuiTextElement(mc, this);

        textbox.flex().w(90);

        element.flex().row(0).preferred(0).height(20);
        element.add(label, textbox.removeTooltip());

        return Arrays.asList(element.tooltip(IKey.lang(this.getConfig().getValueCommentKey(this))));
    }

    @Override
    public void valueFromJSON(JsonElement element)
    {
        this.set(element.getAsString());
    }

    @Override
    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value);
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        this.set(value);

        return true;
    }

    @Override
    public void copy(Value value)
    {
        if (value instanceof ValueString)
        {
            this.value = ((ValueString) value).value;
        }
    }

    @Override
    public void copyServer(Value value)
    {
        if (value instanceof ValueString)
        {
            this.serverValue = ((ValueString) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);

        this.value = ByteBufUtils.readUTF8String(buffer);
        this.defaultValue = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);

        ByteBufUtils.writeUTF8String(buffer, this.value == null ? "" : this.value);
        ByteBufUtils.writeUTF8String(buffer, this.defaultValue == null ? "" : this.defaultValue);
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}