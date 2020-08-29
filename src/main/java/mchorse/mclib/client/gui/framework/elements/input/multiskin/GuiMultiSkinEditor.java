package mchorse.mclib.client.gui.framework.elements.input.multiskin;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import net.minecraft.client.Minecraft;

public class GuiMultiSkinEditor extends GuiElement
{
	public GuiTexturePicker picker;

	public GuiMultiSkinEditor(Minecraft mc, GuiTexturePicker picker)
	{
		super(mc);

		this.picker = picker;
	}
}