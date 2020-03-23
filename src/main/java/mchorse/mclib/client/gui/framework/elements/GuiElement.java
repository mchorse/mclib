package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.resizers.IResizer;
import mchorse.mclib.client.gui.utils.resizers.Resizer;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class GuiElement extends Gui implements IGuiElement
{
    /**
     * Area of this element (i.e. position and size) 
     */
    public Area area = new Area();

    /**
     * Resizer of this class
     */
    protected IResizer resizer;

    /**
     * Tooltip instance
     */
    public GuiTooltip.Tooltip tooltip;

    /**
     * Context menu supplier
     */
    public Supplier<GuiContextMenu> contextMenu;

    /**
     * Hide tooltip
     */
    public boolean hideTooltip;

    /**
     * Whether this element is a container
     */
    protected boolean container;

    /**
     * Parent GUI element
     */
    protected GuiElement parent;

    /**
     * Children elements
     */
    private GuiElements<IGuiElement> children;

    /**
     * Whether this element is enabled (can handle any input) 
     */
    protected boolean enabled = true;

    /**
     * Whether this element is visible 
     */
    protected boolean visible = true;

    /* Useful references */
    protected Minecraft mc;
    protected FontRenderer font;

    /**
     * Initiate GUI element with Minecraft's instance 
     */
    public GuiElement(Minecraft mc)
    {
        this.mc = mc;
        this.font = mc.fontRendererObj;
    }

    /* Hierarchy management */

    public GuiElement getParent()
    {
        return this.parent;
    }

    public boolean hasParent()
    {
        return this.parent != null;
    }

    public List<IGuiElement> getChildren()
    {
        if (this.children == null)
        {
            return Collections.emptyList();
        }

        return this.children.elements;
    }

    public void clear()
    {
        if (this.children == null)
        {
            return;
        }

        for (IGuiElement element : this.children.elements)
        {
            if (element instanceof GuiElement)
            {
                ((GuiElement) element).parent = null;
            }
        }

        this.children.clear();
    }

    public void add(IGuiElement element)
    {
        if (this.children == null)
        {
            this.children = new GuiElements<IGuiElement>(this);
        }

        this.addChild(element);
    }

    public void add(IGuiElement... elements)
    {
        if (this.children == null)
        {
            this.children = new GuiElements<IGuiElement>(this);
        }

        for (IGuiElement element : elements)
        {
            this.addChild(element);
        }
    }

    private void addChild(IGuiElement element)
    {
        if (element instanceof GuiElement)
        {
            ((GuiElement) element).parent = this;
        }

        this.children.add(element);
    }

    public void removeFromParent()
    {
        if (this.hasParent())
        {
            this.parent.remove(this);
        }
    }

    public void remove(GuiElement element)
    {
        if (this.children.elements.remove(element))
        {
            element.parent = null;
        }
    }

    /* Setters */

    public GuiElement tooltip(String label, Direction direction)
    {
        this.tooltip = new GuiTooltip.Tooltip(label, direction);

        return this;
    }

    public GuiElement tooltip(String label, int width, Direction direction)
    {
        this.tooltip = new GuiTooltip.Tooltip(label, width, direction);

        return this;
    }

    public GuiElement hideTooltip()
    {
        this.hideTooltip = true;

        return this;
    }

    /* Container stuff */

    public GuiElement markContainer()
    {
        this.container = true;

        return this;
    }

    public boolean isContainer()
    {
        return this.container;
    }

    public GuiElement getParentContainer()
    {
        GuiElement element = this.getParent();

        while (element != null && !element.isContainer())
        {
            element = element.getParent();
        }

        return element;
    }

    public GuiElement context(Supplier<GuiContextMenu> supplier)
    {
        this.contextMenu = supplier;

        return this;
    }

    /* Resizer methods */

    public Resizer resizer()
    {
        if (this.resizer == null)
        {
            this.resizer = new Resizer();
        }

        return this.resizer instanceof Resizer ? (Resizer) this.resizer : null;
    }

    public IResizer getResizer()
    {
        return this.resizer;
    }

    public GuiElement setResizer(IResizer resizer)
    {
        this.resizer = resizer;

        return this;
    }

    /* Enabled methods */

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

    public void toggleVisible()
    {
        this.visible = !this.visible;
    }

    /* Overriding those methods so it would be much easier to 
     * override only needed methods in subclasses */

    @Override
    public void resize()
    {
        if (this.resizer != null)
        {
            this.resizer.apply(this.area);
        }

        if (this.children != null)
        {
            this.children.resize();
        }
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (this.children != null && this.children.mouseClicked(context))
        {
            return true;
        }

        if (this.area.isInside(context.mouseX, context.mouseY) && context.mouseButton == 1)
        {
            if (!context.hasContextMenu())
            {
                GuiContextMenu menu = this.createContextMenu();

                if (menu != null)
                {
                    context.setContextMenu(menu);

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Create a context menu instance
     *
     * Some subclasses of GuiElement might want to override this method in order to create their
     * own context menus.
     */
    protected GuiContextMenu createContextMenu()
    {
        return this.contextMenu == null ? null : this.contextMenu.get();
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        return this.children != null && this.children.mouseScrolled(context);
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        if (this.children != null)
        {
            this.children.mouseReleased(context);
        }
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        return this.children != null && this.children.keyTyped(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.tooltip != null && this.area.isInside(context.mouseX, context.mouseY))
        {
            context.tooltip.set(this, this.tooltip);
        }
        else if ((this.hideTooltip || this.container) && this.area.isInside(context.mouseX, context.mouseY))
        {
            context.reset();
        }

        if (this.children != null)
        {
            this.children.draw(context);
        }
    }
}