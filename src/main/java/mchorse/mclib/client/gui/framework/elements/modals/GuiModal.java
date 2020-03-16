package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Parent class for all modals
 * 
 * Best to be used with {@link GuiDelegateElement}.
 */
public abstract class GuiModal extends GuiElement
{
    public GuiDelegateElement<IGuiElement> parent;
    public String label = "";

    public GuiModal(Minecraft mc, GuiDelegateElement<IGuiElement> parent, String label)
    {
        super(mc);

        this.parent = parent;
        this.label = label;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return super.mouseClicked(context) || this.area.isInside(context.mouseX, context.mouseY);
    }

    @Override
    public void draw(GuiContext context)
    {
        Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0xcc000000);
        GlStateManager.enableAlpha();
        this.font.drawSplitString(this.label, this.area.x + 10, this.area.y + 10, this.area.w - 20, 0xffffff);

        super.draw(context);
    }
}