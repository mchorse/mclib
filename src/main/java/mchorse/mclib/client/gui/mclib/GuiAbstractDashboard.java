package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Keybind;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.events.RegisterDashboardPanels;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;

public abstract class GuiAbstractDashboard extends GuiBase
{
    public GuiDashboardPanels panels;
    public GuiDashboardPanel defaultPanel;

    private boolean wasClosed = true;
    private int opLevel = -1;

    public GuiAbstractDashboard(Minecraft mc)
    {
        this.panels = this.createDashboardPanels(mc);

        this.panels.flex().relative(this.viewport).wh(1F, 1F);
        this.registerPanels(mc);

        McLib.EVENT_BUS.post(new RegisterDashboardPanels(this));

        this.root.add(this.panels);
    }

    protected abstract GuiDashboardPanels createDashboardPanels(Minecraft mc);

    protected abstract void registerPanels(Minecraft mc);

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        this.close();
        super.onGuiClosed();
    }

    private void close()
    {
        this.panels.close();
        this.wasClosed = true;
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        this.updateOPAccess();

        if (this.wasClosed)
        {
            this.wasClosed = false;
            this.panels.open();
            this.panels.setPanel(this.panels.view.delegate);
        }

        super.setWorldAndResolution(mc, width, height);
    }

    private void updateOPAccess()
    {
        int newOpLevel = OpHelper.getPlayerOpLevel();

        if (newOpLevel != this.opLevel)
        {
            for (GuiDashboardPanel panel : this.panels.panels)
            {
                GuiIconElement button = this.panels.getButton(panel);
                boolean enabled = panel.canBeOpened(newOpLevel);

                button.setEnabled(enabled);

                for (Keybind keybind : this.panels.keys().keybinds)
                {
                    keybind.active(enabled);
                }
            }
        }

        GuiDashboardPanel current = this.panels.view.delegate;

        if (current != null && !current.canBeOpened(newOpLevel))
        {
            this.panels.setPanel(this.defaultPanel);
        }
        else if (current == null)
        {
            this.panels.setPanel(null);
        }

        this.opLevel = newOpLevel;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.panels.view.delegate != null && this.panels.view.delegate.needsBackground())
        {
            GuiDraw.drawCustomBackground(0, 0, this.width, this.height);
        }
        else
        {
            this.drawGradientRect(0, 0, this.width, this.height / 8, 0x44000000, 0);
            this.drawGradientRect(0, this.height - this.height / 8, this.width, this.height, 0, 0x44000000);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}