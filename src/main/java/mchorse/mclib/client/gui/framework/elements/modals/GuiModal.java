package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Supplier;

/**
 * Parent class for all modals
 * 
 * Best to be used with {@link GuiDelegateElement}.
 */
public abstract class GuiModal extends GuiElement
{
    public IKey label;
    public int y;

    public GuiElement bar;

    public static boolean hasModal(GuiElement parent)
    {
        for (IGuiElement element : parent.getChildren())
        {
            if (element instanceof GuiModal)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean addModal(GuiElement parent, Supplier<GuiModal> supplier)
    {
        if (hasModal(parent) || supplier == null)
        {
            return false;
        }

        GuiModal modal = supplier.get();

        modal.resize();
        parent.add(modal);

        return true;
    }

    public static boolean addFullModal(GuiElement parent, Supplier<GuiModal> supplier)
    {
        if (hasModal(parent) || supplier == null)
        {
            return false;
        }

        GuiModal modal = supplier.get();

        modal.flex().relative(parent).wh(1F, 1F);
        modal.resize();
        parent.add(modal);

        return true;
    }

    public GuiModal(Minecraft mc, IKey label)
    {
        super(mc);

        this.bar = new GuiElement(mc);
        this.bar.flex().relative(this).y(1F).w(1F).h(40).anchorY(1F).row(10).padding(10);
        this.add(this.bar);

        this.label = label;
        this.markContainer();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return super.mouseClicked(context) || this.area.isInside(context);
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        return super.mouseScrolled(context) || this.area.isInside(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 0xcc000000);
        GlStateManager.enableAlpha();

        this.y = 0;
        int y = this.area.y + 10;

        for (String line : this.font.listFormattedStringToWidth(this.label.get(), this.area.w - 20))
        {
            this.font.drawStringWithShadow(line, this.area.x + 10, y + this.y, 0xffffff);
            this.y += 11;
        }

        super.draw(context);
    }
}