package mchorse.mclib.client.gui.framework;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;

public class GuiTooltip
{
    public GuiElement element;
    public Tooltip tooltip;
    public Area area = new Area();

    public void set(GuiElement element, Tooltip tooltip)
    {
        this.element = element;
        this.tooltip = tooltip;
        this.area.copy(element.area);
    }

    public void draw(FontRenderer font, int width, int height)
    {
        if (this.element != null)
        {
            int x = this.area.getX(1) + 6;
            int y = this.area.getY(0.5F) - font.FONT_HEIGHT / 2;
            int w = font.getStringWidth(this.tooltip.label);

            if (this.tooltip.direction == TooltipDirection.TOP)
            {
                x = this.area.getX(0.5F) - w / 2;
                y = this.area.y - font.FONT_HEIGHT - 6;
            }
            else if (this.tooltip.direction == TooltipDirection.LEFT)
            {
                x = this.area.x - 6 - w;
            }
            else if (this.tooltip.direction == TooltipDirection.BOTTOM)
            {
                x = this.area.getX(0.5F) - w / 2;
                y = this.area.getY(1) + 6;
            }

            x = MathHelper.clamp_int(x, 6, width - w - 6);
            y = MathHelper.clamp_int(y, 6, height - font.FONT_HEIGHT - 6);

            Gui.drawRect(x - 3, y - 3, x + w + 3, y + font.FONT_HEIGHT + 3, 0xffffffff);
            font.drawStringWithShadow(this.tooltip.label, x, y, 0x000000);
        }
    }

    public static enum TooltipDirection
    {
        TOP, LEFT, BOTTOM, RIGHT;
    }

    public static class Tooltip
    {
        public String label;
        public TooltipDirection direction;

        public Tooltip(String label, TooltipDirection direction)
        {
            this.label = label;
            this.direction = direction;
        }
    }
}