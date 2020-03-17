package mchorse.mclib.client.gui.widgets.buttons;

import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Textured button
 *
 * This button is like regular {@link GuiButton}, but it gets drawn as a
 * texture icon.
 */
public class GuiTextureButton extends GuiButton
{
    public Icon icon;
    public Icon iconHover;
    public int hoverColor = 0xffe6e6e6;

    public GuiTextureButton(int id, int x, int y, Icon icon)
    {
        super(id, x, y, 16, 16, "");
        this.icon = icon;
    }

    public GuiTextureButton setHovered(Icon icon)
    {
        this.iconHover = icon;

        return this;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            GuiUtils.bindColor(this.hovered ? this.hoverColor : 0xffffffff);
            (this.hovered ? this.iconHover : this.icon).render(this.xPosition, this.yPosition);
        }
    }
}