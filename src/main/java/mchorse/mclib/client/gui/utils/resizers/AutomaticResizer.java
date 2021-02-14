package mchorse.mclib.client.gui.utils.resizers;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AutomaticResizer extends BaseResizer
{
    public GuiElement parent;
    public int margin;
    public int padding;
    public int height;

    public AutomaticResizer(GuiElement parent, int margin)
    {
        this.parent = parent;
        this.margin = margin;

        this.setup();
    }

    /* Standard properties */

    public AutomaticResizer padding(int padding)
    {
        this.padding = padding;

        return this;
    }

    public AutomaticResizer height(int height)
    {
        this.height = height;

        return this;
    }

    public void reset()
    {
        /* ¯\_(ツ)_/¯ */
    }

    /* Child management */

    public void setup()
    {
        for (IGuiElement child : this.parent.getChildren())
        {
            if (child instanceof GuiElement)
            {
                GuiElement element = (GuiElement) child;

                element.resizer(this.child(element));
            }
        }
    }

    public IResizer child(GuiElement element)
    {
        ChildResizer child = new ChildResizer(this, element);

        return child;
    }

    public List<ChildResizer> getResizers()
    {
        List<ChildResizer> resizers = new ArrayList<ChildResizer>();

        for (IGuiElement element : this.parent.getChildren())
        {
            if (element instanceof GuiElement)
            {
                GuiElement elem = (GuiElement) element;

                if (elem.resizer() instanceof ChildResizer)
                {
                    resizers.add((ChildResizer) elem.resizer());
                }
            }
        }

        return resizers;
    }

    /* Miscellaneous */

    @Override
    public void add(GuiElement parent, GuiElement child)
    {
        if (child.ignored)
        {
            return;
        }

        child.resizer(this.child(child));
    }

    @Override
    public void remove(GuiElement parent, GuiElement child)
    {
        if (child.ignored)
        {
            return;
        }

        IResizer resizer = child.resizer();

        if (resizer instanceof ChildResizer)
        {
            child.resizer(((ChildResizer) resizer).resizer);
        }
    }

    @Override
    public int getX()
    {
        return 0;
    }

    @Override
    public int getY()
    {
        return 0;
    }

    @Override
    public int getW()
    {
        return 0;
    }

    @Override
    public int getH()
    {
        return 0;
    }
}