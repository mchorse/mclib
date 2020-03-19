package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface IConfigValue
{
	public String getId();

	public void reset();

	public boolean isVisible();

	@SideOnly(Side.CLIENT)
	public List<GuiElement> getFields(Minecraft mc, Config config, ConfigCategory category);

	public void fromJSON(JsonElement element);

	public JsonElement toJSON();
}