package mchorse.mclib.events;

import mchorse.mclib.client.gui.mclib.GuiAbstractDashboard;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RegisterDashboardPanels extends Event
{
    public final GuiAbstractDashboard dashboard;

    public RegisterDashboardPanels(GuiAbstractDashboard dashboard)
    {
        this.dashboard = dashboard;
    }
}