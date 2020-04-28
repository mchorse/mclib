package mchorse.mclib.client;

import mchorse.mclib.ClientProxy;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.events.RemoveDashboardPanels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyboardHandler
{
	public KeyBinding dashboard;

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
			Minecraft.getMinecraft().displayGuiScreen(ClientProxy.getDashboard());
		}
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event)
	{
		if (event.getGui() instanceof GuiMainMenu)
		{
			ClientProxy.dashboard = null;
			McLib.EVENT_BUS.post(new RemoveDashboardPanels());
		}
	}
}