package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;

public class GuiDashboardPanel <T extends GuiAbstractDashboard> extends GuiElement
{
    public final T dashboard;

    public GuiDashboardPanel(Minecraft mc, T dashboard)
    {
        super(mc);

        this.dashboard = dashboard;
        this.markContainer();
    }

    public boolean canBeOpened(int opLevel)
    {
        return this.isClientSideOnly() || OpHelper.isOp(opLevel);
    }

    public boolean isClientSideOnly()
    {
        return false;
    }

    public boolean needsBackground()
    {
        return true;
    }

    public void appear()
    {}

    public void disappear()
    {}

    public void open()
    {}

    public void close()
    {}
}