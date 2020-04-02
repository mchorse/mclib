package mchorse.mclib.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.resizers.layout.RowResizer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Elements
{
	public static GuiElement row(Minecraft mc, int margin, GuiElement... elements)
	{
		return row(mc, margin, 0, elements);
	}

	public static GuiElement row(Minecraft mc, int margin, int padding, GuiElement... elements)
	{
		GuiElement element = new GuiElement(mc);

		RowResizer.apply(element, margin).padding(padding);
		element.add(elements);

		return element;
	}
}