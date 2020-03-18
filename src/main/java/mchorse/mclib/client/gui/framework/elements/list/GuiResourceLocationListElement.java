package mchorse.mclib.client.gui.framework.elements.list;

import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

/**
 * Similar to {@link GuiStringListElement}, but uses {@link ResourceLocation}s 
 */
public class GuiResourceLocationListElement extends GuiListElement<ResourceLocation>
{
    public GuiResourceLocationListElement(Minecraft mc, Consumer<ResourceLocation> callback)
    {
        super(mc, callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    public void sort()
    {
        Collections.sort(this.list, (a, b) -> a.toString().compareToIgnoreCase(b.toString()));
    }

    @Override
    public void drawElement(ResourceLocation element, int i, int x, int y, boolean hover)
    {
        if (this.current == i)
        {
            Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x88000000 + McLib.primaryColor.get());
        }

        this.font.drawStringWithShadow(element.toString(), x + 4, y + 4, hover ? 16777120 : 0xffffff);
    }
}