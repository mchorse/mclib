package mchorse.mclib.events;

import mchorse.mclib.client.gui.mclib.GuiDashboard;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RegisterDashboardPanels extends Event
{
    public final GuiDashboard dashboard;

    public RegisterDashboardPanels(GuiDashboard dashboard)
    {
        this.dashboard = dashboard;
    }
}