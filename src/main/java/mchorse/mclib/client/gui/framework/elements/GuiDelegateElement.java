package mchorse.mclib.client.gui.framework.elements;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Delegated {@link IGuiElement}
 */
@SideOnly(Side.CLIENT)
public class GuiDelegateElement<T extends IGuiElement> extends GuiElement implements IGuiLegacy
{
    public T delegate;

    public GuiDelegateElement(Minecraft mc, T element)
    {
        super(mc);
        this.delegate = element;
    }

    public void setDelegate(T element)
    {
        this.delegate = element;
        this.resize();
    }

    @Override
    public boolean isEnabled()
    {
        return this.delegate != null && this.delegate.isEnabled();
    }

    @Override
    public boolean isVisible()
    {
        return this.delegate == null || this.delegate.isVisible();
    }

    @Override
    public void resize()
    {
        if (this.delegate instanceof GuiElement)
        {
            ((GuiElement) this.delegate).resizer = this.resizer;
        }

        if (this.delegate != null)
        {
            this.delegate.resize();
        }
    }

    @Override
    public boolean handleMouseInput(int mouseX, int mouseY) throws IOException
    {
        if (this.delegate instanceof IGuiLegacy)
        {
            return ((IGuiLegacy) this.delegate).handleMouseInput(mouseX, mouseY);
        }

        return false;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return this.delegate != null && this.delegate.mouseClicked(context);
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        return this.delegate != null && this.delegate.mouseScrolled(context);
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        if (this.delegate != null)
        {
            this.delegate.mouseReleased(context);
        }
    }

    @Override
    public boolean handleKeyboardInput() throws IOException
    {
        if (this.delegate instanceof IGuiLegacy)
        {
            return ((IGuiLegacy) this.delegate).handleKeyboardInput();
        }

        return false;
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        return this.delegate != null && this.delegate.keyTyped(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.delegate != null)
        {
            this.delegate.draw(context);
        }
    }
}