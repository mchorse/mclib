package mchorse.mclib.client.gui.framework.elements.list;

import mchorse.mclib.utils.Interpolation;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Interpolations list
 */
public class GuiInterpolationList extends GuiListElement<Interpolation>
{
    public GuiInterpolationList(Minecraft mc, Consumer<List<Interpolation>> callback)
    {
        super(mc, callback);

        this.scroll.scrollItemSize = 16;

        for (Interpolation interp : Interpolation.values())
        {
            this.add(interp);
        }

        this.sort();
        this.background();
    }

    @Override
    protected boolean sortElements()
    {
        Collections.sort(this.list, Comparator.comparing(o -> o.key));

        return true;
    }

    @Override
    protected String elementToString(Interpolation element, int i, int x, int y, boolean hover, boolean selected)
    {
        return element.getName();
    }
}