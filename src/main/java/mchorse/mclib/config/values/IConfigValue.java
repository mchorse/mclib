package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.config.gui.GuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.function.Consumer;

public interface IConfigValue
{
    public String getId();

    public void reset();

    public boolean isVisible();

    @SideOnly(Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfig gui);

    public void fromJSON(JsonElement element);

    public JsonElement toJSON();
}