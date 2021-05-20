package mchorse.mclib.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.keys.IKey;
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
        return row(mc, margin, padding, 0, elements);
    }

    public static GuiElement row(Minecraft mc, int margin, int padding, int height, GuiElement... elements)
    {
        GuiElement element = new GuiElement(mc);

        element.flex().row(margin).padding(padding).height(height);
        element.add(elements);

        return element;
    }

    public static GuiElement column(Minecraft mc, int margin, GuiElement... elements)
    {
        return column(mc, margin, 0, elements);
    }

    public static GuiElement column(Minecraft mc, int margin, int padding, GuiElement... elements)
    {
        return row(mc, margin, padding, 0, elements);
    }

    public static GuiElement column(Minecraft mc, int margin, int padding, int height, GuiElement... elements)
    {
        GuiElement element = new GuiElement(mc);

        element.flex().column(margin).vertical().stretch().padding(padding).height(height);
        element.add(elements);

        return element;
    }

    public static GuiLabel label(IKey label)
    {
        return label(label, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
    }

    public static GuiLabel label(IKey label, int height)
    {
        return label(label, height, 0xffffff);
    }

    public static GuiLabel label(IKey label, int height, int color)
    {
        GuiLabel element = new GuiLabel(Minecraft.getMinecraft(), label, color);

        element.flex().h(height);

        return element;
    }
}