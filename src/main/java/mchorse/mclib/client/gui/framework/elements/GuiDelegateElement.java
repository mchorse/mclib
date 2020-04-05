package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Delegated {@link IGuiElement}
 */
@SideOnly(Side.CLIENT)
public class GuiDelegateElement<T extends GuiElement> extends GuiElement
{
    public T delegate;

    public GuiDelegateElement(Minecraft mc, T element)
    {
        super(mc);
        this.delegate = element;

        if (this.delegate != null)
        {
            this.delegate.parent = this;
        }
    }

    public void setDelegate(T element)
    {
        this.delegate = element;

        if (this.delegate != null)
        {
            this.delegate.parent = this;
        }

        this.resize();
    }

    private void unsupported()
    {
        throw new IllegalStateException("Following method is unsupported by delegate element!");
    }

    @Override
    public List<IGuiElement> getChildren()
    {
        return this.delegate == null ? Collections.emptyList() : Arrays.asList(this.delegate);
    }

    @Override
    public void clear()
    {
        this.unsupported();
    }

    @Override
    public void add(IGuiElement element)
    {
        this.unsupported();
    }

    @Override
    public void add(IGuiElement... elements)
    {
        this.unsupported();
    }

    @Override
    public void remove(GuiElement element)
    {
        if (this.delegate != null && this.delegate == element)
        {
            this.delegate.parent = null;
            this.delegate = null;
        }
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
        if (this.delegate != null)
        {
            this.delegate.resizer = this.resizer;
            this.delegate.flex().link(this.flex());
            this.delegate.resize();
        }
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return this.delegate != null && this.delegate.mouseClicked(context);
    }

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        return this.delegate == null ? super.createContextMenu(context) : this.delegate.createContextMenu(context);
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