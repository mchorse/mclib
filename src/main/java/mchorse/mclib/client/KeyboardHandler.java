package mchorse.mclib.client;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.config.values.ValueRL;
import mchorse.mclib.events.RemoveDashboardPanels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyboardHandler
{
    public KeyBinding dashboard;

    private int lastGuiScale = -1;

    public KeyboardHandler()
    {
        this.dashboard = new KeyBinding("key.mclib.dashboard", Keyboard.KEY_0, "key.mclib.category");

        ClientRegistry.registerKeyBinding(this.dashboard);
    }

    @SubscribeEvent
    public void onKeyboardInput(InputEvent.KeyInputEvent event)
    {
        if (this.dashboard.isPressed())
        {
            GuiDashboard dashboard = GuiDashboard.get();

            Minecraft.getMinecraft().displayGuiScreen(dashboard);

            if (GuiScreen.isCtrlKeyDown())
            {
                dashboard.panels.setPanel(dashboard.config);
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.getGui() instanceof GuiBase)
        {
            if (this.lastGuiScale == -1)
            {
                this.lastGuiScale = Minecraft.getMinecraft().gameSettings.guiScale;

                int scale = McLib.userIntefaceScale.get();

                if (scale > 0)
                {
                    Minecraft.getMinecraft().gameSettings.guiScale = scale;
                }
            }
        }
        else
        {
            if (this.lastGuiScale != -1)
            {
                Minecraft.getMinecraft().gameSettings.guiScale = this.lastGuiScale;
                this.lastGuiScale = -1;
            }

            if (event.getGui() instanceof GuiMainMenu)
            {
                GuiDashboard.dashboard = null;
                ValueRL.picker = null;
                McLib.EVENT_BUS.post(new RemoveDashboardPanels());
            }
        }
    }
}