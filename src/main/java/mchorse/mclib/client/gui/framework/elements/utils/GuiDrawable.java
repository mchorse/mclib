package mchorse.mclib.client.gui.framework.elements.utils;

import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;

public class GuiDrawable implements IGuiElement
{
    public Consumer<GuiContext> callback;

    public GuiDrawable(Consumer<GuiContext> callback)
    {
        this.callback = callback;
    }

    @Override
    public void resize()
    {}

    @Override
    public boolean isEnabled()
    {
        return false;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        return false;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {}

    @Override
    public boolean keyTyped(GuiContext context)
    {
        return false;
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.callback != null)
        {
            this.callback.accept(context);
        }
    }
}