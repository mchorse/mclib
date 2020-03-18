package mchorse.mclib.client.gui.framework;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.utils.Direction;
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

        if (element != null)
        {
            this.area.copy(element.area);
        }
    }

    public void draw(FontRenderer font, int width, int height)
    {
        if (this.element != null)
        {
            int x = this.area.getX(1) + 6;
            int y = this.area.getY(0.5F) - font.FONT_HEIGHT / 2;
            int w = font.getStringWidth(this.tooltip.label);

            if (this.tooltip.direction == Direction.TOP)
            {
                x = this.area.getX(0.5F) - w / 2;
                y = this.area.y - font.FONT_HEIGHT - 6;
            }
            else if (this.tooltip.direction == Direction.LEFT)
            {
                x = this.area.x - 6 - w;
            }
            else if (this.tooltip.direction == Direction.BOTTOM)
            {
                x = this.area.getX(0.5F) - w / 2;
                y = this.area.getY(1) + 6;
            }

            x = MathHelper.clamp_int(x, 6, width - w - 6);
            y = MathHelper.clamp_int(y, 6, height - font.FONT_HEIGHT - 6);

            Gui.drawRect(x - 3, y - 3, x + w + 3, y + font.FONT_HEIGHT + 3, 0xffffffff);
            font.drawString(this.tooltip.label, x, y + 1, 0x000000);
        }
    }

    public static class Tooltip
    {
        public String label;
        public Direction direction;

        public Tooltip(String label, Direction direction)
        {
            this.label = label;
            this.direction = direction;
        }
    }
}