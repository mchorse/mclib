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

	public Keybind register(String label, int key, Supplier<Boolean> callback)
	{
		Keybind keybind = new Keybind(label, key, callback);

		this.keybinds.add(keybind);

		return keybind;
	}

	public Keybind registerInside(String label, int key, Supplier<Boolean> callback)
	{
		return this.register(label, key, callback).inside();
	}

	public void add(GuiKeybinds keybinds, boolean inside)
	{
		if (!keybinds.isVisible())
		{
			return;
		}

		for (Keybind keybind : this.keybinds)
		{
			if (keybind.active && (!keybind.inside || inside))
			{
				keybinds.addKeybind(keybind);
			}
		}
	}

	public boolean check(int keyCode, boolean inside)
	{
		for (Keybind keybind : this.keybinds)
		{
			if (keybind.active && keybind.check(keyCode, inside))
			{
				return keybind.callback != null && keybind.callback.get();
			}
		}

		return false;
	}
}