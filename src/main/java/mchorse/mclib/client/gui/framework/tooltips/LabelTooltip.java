package mchorse.mclib.client.gui.framework.tooltips;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.gui.Gui;

import java.util.List;

public class LabelTooltip implements ITooltip
{
    public IKey label;
    public int width = 200;
    public Direction direction;

    public LabelTooltip(IKey label, Direction direction)
    {
        this.label = label;
        this.direction = direction;
    }

    public LabelTooltip(IKey label, int width, Direction direction)
    {
        this(label, direction);
        this.width = width;
    }

    public void drawTooltip(GuiContext context)
    {
        String label = this.label.get();

        if (label.isEmpty())
        {
            return;
        }

        List<String> strings = context.font.listFormattedStringToWidth(label, this.width);

        if (strings.isEmpty())
        {
            return;
        }

        Direction dir = this.direction;
        Area area = context.tooltip.area;
        int w = strings.size() == 1 ? context.font.getStringWidth(strings.get(0)) : this.width;
        int h = (context.font.FONT_HEIGHT + 3) * strings.size() - 3;
        int x = area.x(dir.anchorX) - (int) (w * (1 - dir.anchorX)) + 6 * dir.factorX;
        int y = area.y(dir.anchorY) - (int) (h * (1 - dir.anchorY)) + 6 * dir.factorY;

        x = MathUtils.clamp(x, 3, context.screen.width - w - 3);
        y = MathUtils.clamp(y, 3, context.screen.height - h - 3);

        Gui.drawRect(x - 3, y - 3, x + w + 3, y + h + 3, 0xffffffff);

        for (String line : strings)
        {
            context.font.drawString(line, x, y, 0x000000);
            y += context.font.FONT_HEIGHT + 3;
        }
    }
}
