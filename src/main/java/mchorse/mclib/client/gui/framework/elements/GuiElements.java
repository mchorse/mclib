package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI elements collection
 * 
 * This class is responsible for handling a collection of elements
 */
public class GuiElements<T extends IGuiElement> implements IGuiElement
{
    /**
     * List of elements 
     */
    public List<T> elements = new ArrayList<T>();

    /**
     * Whether this element is enabled (can handle any input) 
     */
    protected boolean enabled = true;

    /**
     * Whether this element is visible 
     */
    protected boolean visible = true;

    /**
     * Parent of this elements collection
     */
    private GuiElement parent;

    public GuiElements(GuiElement parent)
    {
        this.parent = parent;
    }

    public void clear()
    {
        this.elements.clear();
    }

    public void prepend(T element)
    {
        if (element != null)
        {
            this.elements.add(0, element);
        }
    }

    public void add(T element)
    {
        if (element != null)
        {
            this.elements.add(element);
        }
    }

    public boolean addAfter(T target, T element)
    {
        int index = this.elements.indexOf(target);

        if (index != -1 && element != null)
        {
            if (index + 1 >= this.elements.size())
            {
                this.elements.add(element);
            }
            else
            {
                this.elements.add(index + 1, element);
            }

            return true;
        }

        return false;
    }

    public boolean addBefore(T target, T element)
    {
        int index = this.elements.indexOf(target);

        if (index != -1 && element != null)
        {
            this.elements.add(index, element);

            return true;
        }

        return false;
    }

    public void add(T... elements)
    {
        for (T element : elements)
        {
            if (element != null) this.elements.add(element);
        }
    }

    @Override
    public void resize()
    {
        for (T element : this.elements)
        {
            element.resize();
        }
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled && this.visible;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.mouseClicked(context))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.mouseScrolled(context))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled())
            {
                element.mouseReleased(context);
            }
        }
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.keyTyped(context))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void draw(GuiContext context)
    {
        for (T element : this.elements)
        {
            if (element.isVisible())
            {
                element.draw(context);
            }
        }
    }
}