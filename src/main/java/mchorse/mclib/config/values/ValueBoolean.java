package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.config.gui.GuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueBoolean extends Value
{
    private boolean value;
    private boolean defaultValue;

    public ValueBoolean(String id, boolean defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;

        this.reset();
    }

    public boolean get()
    {
        return this.value;
    }

    public void set(boolean value)
    {
        this.value = value;
        this.saveLater();
    }

    @Override
    public void reset()
    {
        this.set(this.defaultValue);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfig gui)
    {
        GuiToggleElement toggle = new GuiToggleElement(mc, this);

        toggle.flex().reset();

        return Arrays.asList(toggle);
    }

    @Override
    public void fromJSON(JsonElement element)
    {
        this.set(element.getAsBoolean());
    }

    @Override
    public JsonElement toJSON()
    {
        return new JsonPrimitive(this.value);
    }
}
