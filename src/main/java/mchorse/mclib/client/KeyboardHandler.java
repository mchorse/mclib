package mchorse.mclib.client;

import mchorse.mclib.client.gui.mclib.GuiDashboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyboardHandler
{
	public KeyBinding dashboard;

	public KeyboardHandler()
	{
		String category = "key.mclib.category";

		this.dashboard = new KeyBinding("key.mclib.dashboard", Keyboard.KEY_0, category);

		ClientRegistry.registerKeyBinding(this.dashboard);
	}

	@SubscribeEvent
	public void onKeyboardInput(InputEvent.KeyInputEvent event)
	{
		if (this.dashboard.isPressed())
		{
			Minecraft mc = Minecraft.getMinecraft();

			mc.displayGuiScreen(new GuiDashboard(mc));
		}
	}
}