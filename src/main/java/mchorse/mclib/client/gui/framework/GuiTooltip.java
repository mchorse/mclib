package mchorse.mclib.client.gui.framework;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.gui.Gui;

import java.util.List;

public class GuiTooltip
{
    public GuiElement element;
    public Area area = new Area();

    public void set(GuiElement element)
    {
        this.element = element;

        if (element != null)
        {
            this.area.copy(element.area);
        }
    }

    public void draw(Tooltip tooltip, GuiContext context)
    {
        if (this.element == null || tooltip == null)
        {
            return;
        }

        List<String> strings = context.font.listFormattedStringToWidth(tooltip.label, tooltip.width);

        if (strings.isEmpty())
        {
            return;
        }

        int w = strings.size() == 1 ? context.font.getStringWidth(strings.get(0)) : tooltip.width;
        int h = (context.font.FONT_HEIGHT + 3) * strings.size() - 3;

        int x = this.area.ex() + 6;
        int y = this.area.my() - h / 2;

        if (tooltip.direction == Direction.TOP)
        {
            x = this.area.mx() - w / 2;
            y = this.area.y - h - 6;
        }
        else if (tooltip.direction == Direction.LEFT)
        {
            x = this.area.x - w - 6;
        }
        else if (tooltip.direction == Direction.BOTTOM)
        {
            x = this.area.mx() - w / 2;
            y = this.area.ey() + 6;
        }

        x = MathUtils.clamp(x, 3, context.screen.width - w - 3);
        y = MathUtils.clamp(y, 3, context.screen.height - h - 3);

        Gui.drawRect(x - 3, y - 3, x + w + 3, y + h + 3, 0xffffffff);

        for (String line : strings)
        {
            context.font.drawString(line, x, y, 0x000000);
            y += context.font.FONT_HEIGHT + 3;
        }
    }

    public void drawTooltip(GuiContext context)
    {
        if (this.element != null)
        {
            this.element.drawTooltip(context, this.area);
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