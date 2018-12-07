package mchorse.mclib.client.gui.framework.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mchorse.mclib.client.gui.framework.GuiTooltip;

/**
 * GUI elements collection
 * 
 * This class is responsible for handling a collection of elements
 */
public class GuiElements<T extends IGuiElement> implements IGuiElement, IGuiLegacy
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

    public void add(T element)
    {
        if (element != null) this.elements.add(element);
    }

    @SuppressWarnings("unchecked")
    public void add(T... elements)
    {
        for (T element : elements)
        {
            if (element != null) this.elements.add(element);
        }
    }

    @Override
    public void resize(int width, int height)
    {
        for (T element : this.elements)
        {
            element.resize(width, height);
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
    public boolean handleMouseInput(int mouseX, int mouseY) throws IOException
    {
        for (T element : this.elements)
        {
            if (element instanceof IGuiLegacy)
            {
                if (((IGuiLegacy) element).handleMouseInput(mouseX, mouseY))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.mouseClicked(mouseX, mouseY, mouseButton))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.mouseScrolled(mouseX, mouseY, scroll))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled())
            {
                element.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    @Override
    public boolean hasActiveTextfields()
    {
        for (T element : this.elements)
        {
            if (element.isEnabled() && element.hasActiveTextfields())
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean handleKeyboardInput() throws IOException
    {
        for (T element : this.elements)
        {
            if (element instanceof IGuiLegacy)
            {
                if (((IGuiLegacy) element).handleKeyboardInput())
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        for (T element : this.elements)
        {
            if (element.isEnabled())
            {
                element.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        for (T element : this.elements)
        {
            if (element.isVisible())
            {
                element.draw(tooltip, mouseX, mouseY, partialTicks);
            }
        }
    }
}