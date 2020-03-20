package mchorse.mclib.client.gui.framework;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;

import java.util.List;

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
            List<String> strings = font.listFormattedStringToWidth(this.tooltip.label, this.tooltip.width);

            if (strings.isEmpty())
            {
                return;
            }

            int w = strings.size() == 1 ? font.getStringWidth(strings.get(0)) : this.tooltip.width;
            int h = (font.FONT_HEIGHT + 3) * strings.size() - 3;

            int x = this.area.getX(1) + 6;
            int y = this.area.getY(0.5F) - h / 2;

            if (this.tooltip.direction == Direction.TOP)
            {
                x = this.area.getX(0.5F) - w / 2;
                y = this.area.y - h - 6;
            }
            else if (this.tooltip.direction == Direction.LEFT)
            {
                x = this.area.x - w - 6;
            }
            else if (this.tooltip.direction == Direction.BOTTOM)
            {
                x = this.area.getX(0.5F) - w / 2;
                y = this.area.getY(1) + 6;
            }

            x = MathHelper.clamp_int(x, 3, width - w - 3);
            y = MathHelper.clamp_int(y, 3, height - h - 3);

            Gui.drawRect(x - 3, y - 3, x + w + 3, y + h + 3, 0xffffffff);

            for (String line : strings)
            {
                font.drawString(line, x, y, 0x000000);
                y += font.FONT_HEIGHT + 3;
            }
        }
    }

    public static class Tooltip
    {
        public String label;
        public int width = 200;
        public Direction direction;

        public Tooltip(String label, Direction direction)
        {
            this.label = label;
            this.direction = direction;
        }

        public Tooltip(String label, int width, Direction direction)
        {
            this(label, direction);
            this.width = width;
        }
    }
}