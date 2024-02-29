package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.framework.elements.utils.IconContainer;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

public class GuiCollapseSection extends GuiElement
{
    protected GuiLabel title;
    protected GuiElement fields;
    protected boolean collapsed;
    protected IconContainer collapsedIcon = new IconContainer(Icons.MOVE_RIGHT, 14, 10, 0.5F, 0.5F, -2, -2);
    protected IconContainer openedIcon = new IconContainer(Icons.MOVE_DOWN, 14, 10, 0.5F, 0.5F, -2, 0);

    /**
     * @param mc
     * @param title
     * @param titleBackground the background color of the title as Supplier to allow changes of the color through configurations.
     * @param collapsed true if it should be collapsed by default, false if it should display its fields.
     */
    public GuiCollapseSection(Minecraft mc, IKey title, Supplier<Integer> titleBackground, boolean collapsed)
    {
        super(mc);

        this.title = Elements.label(title).background(titleBackground);
        this.title.setLeftIconContainer(this.collapsedIcon);
        this.fields = new GuiElement(mc);
        this.fields.flex().column(5).stretch().vertical().height(20);

        this.flex().column(5).stretch().vertical();
        super.add(this.title);

        if (!collapsed)
        {
            super.add(this.fields);
            this.title.setLeftIconContainer(this.openedIcon);
        }

        this.collapsed = collapsed;
    }

    public GuiCollapseSection(Minecraft mc, IKey title, Supplier<Integer> titleBackground)
    {
        this(mc, title, titleBackground, false);
    }

    public GuiCollapseSection(Minecraft mc, IKey title)
    {
        this(mc, title, () -> ColorUtils.HALF_BLACK + McLib.primaryColor.get());
    }

    public void setCollapsed(boolean collapsed)
    {
        if (this.collapsed != collapsed)
        {
            this.updateCollapse();
        }
    }

    public boolean isCollapsed()
    {
        return this.collapsed;
    }

    @Deprecated
    public void addField(GuiElement element)
    {
        this.fields.add(element);
    }

    @Deprecated
    public void addFields(GuiElement... element)
    {
        this.fields.add(element);
    }

    @Override
    public void add(IGuiElement... elements)
    {
        this.fields.add(elements);
    }

    @Override
    public void add(IGuiElement element)
    {
        this.fields.add(element);
    }

    public GuiLabel getTitle()
    {
        return this.title;
    }

    protected void updateCollapse()
    {
        if (!this.collapsed)
        {
            this.fields.removeFromParent();
            this.title.setLeftIconContainer(this.collapsedIcon);

            this.collapsed = true;
        }
        else
        {
            super.add(this.fields);
            this.title.setLeftIconContainer(this.openedIcon);

            this.collapsed = false;
        }
    }

    /**
     * Toggle visibility of the field section
     */
    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.title.area.isInside(context))
        {
            this.updateCollapse();

            this.getParent().resize();

            return true;
        }

        return false;
    }
}
