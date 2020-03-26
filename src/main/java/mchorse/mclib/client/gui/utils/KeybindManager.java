package mchorse.mclib.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.input.GuiKeybinds;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Keybind manager
 */
public class KeybindManager
{
	public List<Keybind> keybinds = new ArrayList<Keybind>();

	public KeybindManager register(String label, int key, Supplier<Boolean> callback)
	{
		this.keybinds.add(new Keybind(label, key, callback));

		return this;
	}

	public KeybindManager registerInside(String label, int key, Supplier<Boolean> callback)
	{
		this.keybinds.add(new Keybind(label, key, callback).inside());

		return this;
	}

	public KeybindManager register(String label, int key, Supplier<Boolean> callback, int... keys)
	{
		this.keybinds.add(new Keybind(label, key, callback).held(keys));

		return this;
	}

	public KeybindManager registerInside(String label, int key, Supplier<Boolean> callback, int... keys)
	{
		this.keybinds.add(new Keybind(label, key, callback).inside().held(keys));

		return this;
	}

	public void add(GuiKeybinds keybinds, boolean inside)
	{
		if (!keybinds.isVisible())
		{
			return;
		}

		for (Keybind keybind : this.keybinds)
		{
			if (!keybind.inside || inside)
			{
				keybinds.keybinds.add(keybind);
			}
		}
	}

	public boolean check(int keyCode, boolean inside)
	{
		for (Keybind keybind : this.keybinds)
		{
			if (keybind.check(keyCode, inside))
			{
				return keybind.callback != null && keybind.callback.get();
			}
		}

		return false;
	}
}