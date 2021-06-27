package mchorse.mclib.client.gui.framework.tooltips.styles;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;

public class DarkTooltipStyle extends TooltipStyle
{
    @Override
    public void drawBackground(Area area)
    {
        int color = McLib.primaryColor.get();

        GuiDraw.drawDropShadow(area.x, area.y, area.ex(), area.ey(), 6, 0x44000000 + color, color);
        area.draw(0xff000000);
    }

    @Override
    public int getTextColor()
    {
        return 0xffffff;
    }

    @Override
    public int getForegroundColor()
    {
        return McLib.primaryColor.get();
    }
}