package mchorse.mclib.client.gui.framework.elements;

import com.google.common.collect.ImmutableList;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Resizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public Resizer resizer;

    /**
     * Tooltip instance
     */
    public GuiTooltip.Tooltip tooltip;

    /**
     * Hide tooltip
     */
    public boolean hideTooltip;

    /**
     * Parent GUI element
     */
    private GuiElement parent;

    /**
     * Children elements
     */
    private GuiElements<IGuiElement> children;

    /**
     * Cached children unmodifiable list
     */
    private List<IGuiElement> cachedChilden;

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

        if (this.cachedChilden == null)
        {
            this.cachedChilden = Collections.unmodifiableList(this.children.elements);
        }

        return this.cachedChilden;
    }

    public void add(IGuiElement element)
    {
        if (this.children == null)
        {
            this.children = new GuiElements<IGuiElement>();
        }

        this.addChild(element);
    }

    public void add(IGuiElement... elements)
    {
        if (this.children == null)
        {
            this.children = new GuiElements<IGuiElement>();
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

    /* Tooltip */

    public GuiElement tooltip(String label, GuiTooltip.TooltipDirection direction)
    {
        this.tooltip = new GuiTooltip.Tooltip(label, direction);

        return this;
    }

    public GuiElement hideTooltip()
    {
        this.hideTooltip = true;

        return this;
    }

    /* Resizer methods */

    public Resizer resizer()
    {
        if (this.resizer == null)
        {
            this.resizer = new Resizer();
        }

        return this.resizer;
    }

    public GuiElement setResizer(Resizer resizer)
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
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.children != null)
        {
            return this.children.mouseClicked(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        if (this.children != null)
        {
            return this.children.mouseScrolled(mouseX, mouseY, scroll);
        }

        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.children != null)
        {
            this.children.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public boolean hasActiveTextfields()
    {
        if (this.children != null)
        {
            return this.children.hasActiveTextfields();
        }

        return false;
    }

    @Override
    public void unfocus()
    {
        if (this.children != null)
        {
            this.children.unfocus();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (this.children != null)
        {
            this.children.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.tooltip != null && this.area.isInside(mouseX, mouseY))
        {
            tooltip.set(this, this.tooltip);
        }
        else if (this.hideTooltip && this.area.isInside(mouseX, mouseY))
        {
            tooltip.set(null, null);
        }

        if (this.children != null)
        {
            this.children.draw(tooltip, mouseX, mouseY, partialTicks);
        }
    }
}