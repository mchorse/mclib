package mchorse.mclib.client.gui.framework;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.gui.Gui;

import java.util.List;

public class GuiTooltip
{
    public GuiElement element;
    public Area area = new Area();

    public void set(GuiContext context, GuiElement element)
    {
        this.element = element;

        if (element != null)
        {
            this.area.copy(element.area);
            this.area.x -= context.shiftX;
            this.area.y -= context.shiftY;
        }
    }

    public void draw(Tooltip tooltip, GuiContext context)
    {
        if (this.element == null || tooltip == null)
        {
            return;
        }

        String label = tooltip.label.get();

        if (label.isEmpty())
        {
            return;
        }

        List<String> strings = context.font.listFormattedStringToWidth(label, tooltip.width);

        if (strings.isEmpty())
        {
            return;
        }

        Direction dir = tooltip.direction;
        int w = strings.size() == 1 ? context.font.getStringWidth(strings.get(0)) : tooltip.width;
        int h = (context.font.FONT_HEIGHT + 3) * strings.size() - 3;
        int x = this.area.x(dir.anchorX) - (int) (w * (1 - dir.anchorX)) + 6 * dir.factorX;
        int y = this.area.y(dir.anchorY) - (int) (h * (1 - dir.anchorY)) + 6 * dir.factorY;

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
        public IKey label;
        public int width = 200;
        public Direction direction;

        public Tooltip(IKey label, Direction direction)
        {
            this.label = label;
            this.direction = direction;
        }

        public Tooltip(IKey label, int width, Direction direction)
        {
            this(label, direction);
            this.width = width;
        }
    }
}