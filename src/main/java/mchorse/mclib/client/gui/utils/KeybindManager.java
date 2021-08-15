package mchorse.mclib.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.input.GuiKeybinds;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ModHelper;
import net.minecraftforge.fml.common.ModContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Keybind manager
 */
public class KeybindManager
{
    public List<Keybind> keybinds = new ArrayList<Keybind>();
    public boolean focus = true;

    public Keybind register(IKey label, int key, Runnable callback)
    {
        ModContainer mod = ModHelper.getCallerMod();
        String modid = mod == null ? "" : mod.getModId();

        return this.register(modid, label, key, callback);
    }

    public Keybind registerInside(IKey label, int key, Runnable callback)
    {
        ModContainer mod = ModHelper.getCallerMod();
        String modid = mod == null ? "" : mod.getModId();

        return this.register(modid, label, key, callback).inside();
    }

    private Keybind register(String modid, IKey label, int key, Runnable callback)
    {
        Keybind keybind = new Keybind(modid, label, key, callback);

        this.keybinds.add(keybind);

        return keybind;
    }

    public KeybindManager ignoreFocus()
    {
        this.focus = false;

        return this;
    }

    public void add(GuiContext context, boolean inside)
    {
        if (this.focus && context.isFocused())
        {
            return;
        }

        GuiKeybinds keybinds = context.keybinds;

        if (!keybinds.isVisible())
        {
            return;
        }

        for (Keybind keybind : this.keybinds)
        {
            if (keybind.isActive() && (!keybind.inside || inside))
            {
                keybinds.addKeybind(keybind);
            }
        }
    }

    public boolean check(GuiContext context, boolean inside)
    {
        if (this.focus && context.isFocused())
        {
            return false;
        }

        int keyCode = context.keyCode;

        for (Keybind keybind : this.keybinds)
        {
            if (keybind.isActive() && keybind.check(keyCode, inside) && keybind.callback != null)
            {
                keybind.callback.run();

                return true;
            }
        }

        return false;
    }
}