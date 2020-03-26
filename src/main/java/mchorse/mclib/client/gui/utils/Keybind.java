package mchorse.mclib.client.gui.utils;

import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Keybind class
 */
public class Keybind
{
	public String label;
	public int mainKey;
	public int[] heldKeys;
	public Supplier<Boolean> callback;
	public boolean inside;

	public Keybind(String label, int mainKey, Supplier<Boolean> callback)
	{
		this.label = label;
		this.mainKey = mainKey;
		this.callback = callback;
	}

	public Keybind held(int... keys)
	{
		this.heldKeys = Arrays.copyOf(keys, keys.length);

		return this;
	}

	public Keybind inside()
	{
		this.inside = true;

		return this;
	}

	public String getKeyCombo()
	{
		String label = Keyboard.getKeyName(this.mainKey);

		if (this.heldKeys != null)
		{
			for (int held : this.heldKeys)
			{
				label = Keyboard.getKeyName(held) + " + " + label;
			}
		}

		return label;
	}

	public boolean check(int keyCode, boolean inside)
	{
		if (keyCode != this.mainKey)
		{
			return false;
		}

		if (this.heldKeys != null)
		{
			for (int key : this.heldKeys)
			{
				if (!Keyboard.isKeyDown(key))
				{
					return false;
				}
			}
		}

		if (this.inside)
		{
			return inside;
		}

		return true;
	}
}