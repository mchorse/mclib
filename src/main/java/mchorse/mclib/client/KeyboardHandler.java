package mchorse.mclib.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyboardHandler
{
	public KeyBinding dashboard;

	public KeyboardHandler()
	{
		String category = "key.mclib.category";

		this.dashboard = new KeyBinding("key.mclib.dashboard", Keyboard.KEY_NONE, category);
	}

	@SubscribeEvent
	public void onKeyboardInput(InputEvent.KeyInputEvent event)
	{
		if (this.dashboard.isPressed())
		{
			/* TODO: open dashboard */
		}
	}
}