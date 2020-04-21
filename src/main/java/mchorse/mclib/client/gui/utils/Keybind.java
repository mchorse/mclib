package mchorse.mclib.client.gui.utils;

import org.lwjgl.input.Keyboard;

import java.util.Arrays;

/**
 * Keybind class
 */
public class Keybind
{
	public String label;
	public int mainKey;
	public int[] heldKeys;
	public Runnable callback;
	public boolean inside;
	public boolean active = true;

	public Keybind(String label, int mainKey, Runnable callback)
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

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Keybind)
		{
			Keybind keybind = (Keybind) obj;

			return this.mainKey == keybind.mainKey && Arrays.equals(this.heldKeys, keybind.heldKeys) && this.inside == keybind.inside;
		}

		return super.equals(obj);
	}
}