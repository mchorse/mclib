package mchorse.mclib.client.gui.utils;

import mchorse.mclib.ClientProxy;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.keys.KeyParser;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.utils.Keys;

import java.util.function.Supplier;

/**
 * Keybind class
 */
public class Keybind
{
    public String modid;
    public IKey label;
    public IKey category = IKey.EMPTY;
    public int keyCode;
    public Runnable callback;
    public boolean inside;
    public boolean active = true;
    public Supplier<Boolean> activeSupplier;

    public String labelToken = "";
    public String categoryToken = "";

    public Keybind(String modid, IKey label, int keyCode, Runnable callback)
    {
        this.modid = modid;
        this.label = label;
        this.keyCode = keyCode;
        this.callback = callback;

        this.labelToken = KeyParser.toJson(label);

        ClientProxy.keybinds.addKeybind(this);
    }

    public Keybind held(int... keys)
    {
        this.keyCode = Keys.getComboKeyCode(keys, keyCode);

        ClientProxy.keybinds.addKeybind(this);

        return this;
    }

    public Keybind inside()
    {
        this.inside = true;

        return this;
    }

    public Keybind active(Supplier<Boolean> active)
    {
        this.activeSupplier = active;

        return this;
    }

    public Keybind active(boolean active)
    {
        this.active = active;

        return this;
    }

    public Keybind category(IKey category)
    {
        ClientProxy.keybinds.updateCategory(this, category);

        return this;
    }

    public void setCategory(IKey category)
    {
        this.category = category;
        this.categoryToken = KeyParser.toJson(category);
    }

    public String getKeyCombo()
    {
        ValueInt config = ClientProxy.keybinds.getKeybind(this.modid, this.categoryToken, this.labelToken);

        if (config != null)
        {
            return Keys.getComboKeyName(config.get());
        }
        else
        {
            return Keys.getComboKeyName(this.keyCode);
        }
    }

    public boolean check(int keyCode, boolean inside)
    {
        ValueInt config = ClientProxy.keybinds.getKeybind(this.modid, this.categoryToken, this.labelToken);
        int check = config == null ? this.keyCode : config.get();

        if (Keys.getMainKey(check) != keyCode || !Keys.checkModifierKeys(check))
        {
            return false;
        }

        if (this.inside)
        {
            return inside;
        }

        return true;
    }

    public boolean isActive()
    {
        if (this.activeSupplier != null)
        {
            return this.activeSupplier.get();
        }

        return this.active;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Keybind)
        {
            Keybind keybind = (Keybind) obj;

            return this.keyCode == keybind.keyCode && this.inside == keybind.inside;
        }

        return super.equals(obj);
    }
}