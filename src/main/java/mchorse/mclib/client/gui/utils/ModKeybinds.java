package mchorse.mclib.client.gui.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiKeybindElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.keys.KeyParser;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.config.values.IConfigGuiProvider;
import mchorse.mclib.config.values.Value;
import mchorse.mclib.config.values.ValueInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.ModContainer;

public class ModKeybinds extends Value
{
    public ModContainer mod;

    public ModKeybinds(ModContainer mod)
    {
        super(mod == null ? "" : mod.getModId());

        this.mod = mod;
        this.clientSide();
    }

    public void addKeybind(Keybind key)
    {
        String categoryId = key.categoryToken;
        String id = key.labelToken;

        Value category = this.getSubValue(categoryId);

        if (category == null)
        {
            category = new KeybindCategory(categoryId);
            category.setConfig(this.getConfig());

            this.addSubValue(category);
        }

        if (category.getSubValue(id) != null)
        {
            ValueInt old = (ValueInt) category.getSubValue(id);

            if (old.hasChanged())
            {
                return;
            }
        }

        ValueInt keybind = new ValueInt(id, key.keyCode).comboKey();

        keybind.setConfig(this.getConfig());
        category.addSubValue(keybind);
    }

    @Override
    public boolean isVisible()
    {
        int n = 0;

        for (Value value : this.getSubValues())
        {
            n += value.getSubValues().size();
        }

        return n != 0;
    }

    @Override
    public String getLabelKey()
    {
        if (this.mod == null)
        {
            return "keybinds.config.unknown_mod";
        }
        else
        {
            String key = this.id + ".config.title";

            if (I18n.hasKey(key))
            {
                return key;
            }
            else
            {
                return this.mod.getName() + ' ';
            }
        }
    }

    @Override
    public void setConfig(Config config)
    {
        super.setConfig(config);

        Value category = new KeybindCategory("");

        category.setConfig(this.getConfig());
        this.addSubValue(category);
    }

    @Override
    public void fromJSON(JsonElement element)
    {
        if (element.isJsonObject())
        {
            for (Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet())
            {
                String category = entry.getKey();
                Value value = this.getSubValue(category);

                if (value == null)
                {
                    value = new KeybindCategory(category);
                    value.setConfig(this.getConfig());

                    this.addSubValue(value);
                }

                JsonElement children = entry.getValue();

                if (children.isJsonObject())
                {
                    for (Entry<String, JsonElement> keybind : children.getAsJsonObject().entrySet())
                    {
                        JsonElement keyCode = keybind.getValue();

                        if (keyCode.isJsonPrimitive() && keyCode.getAsJsonPrimitive().isNumber())
                        {
                            ValueInt key = new ValueInt(keybind.getKey(), -1).comboKey();

                            key.set(keyCode.getAsInt());
                            key.setConfig(this.getConfig());

                            value.addSubValue(key);
                        }
                    }
                }
            }
        }
    }

    public static class KeybindCategory extends Value implements IConfigGuiProvider
    {
        public static final IKey NO_CATEGORY = IKey.lang("keybinds.config.no_category");

        public KeybindCategory(String id)
        {
            super(id);
        }

        @Override
        public boolean isVisible()
        {
            return !this.getSubValues().isEmpty();
        }

        @Override
        public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
        {
            List<GuiElement> elements = new ArrayList<GuiElement>();

            GuiElement label = Elements.label(this.id.isEmpty() ? NO_CATEGORY : KeyParser.fromJson(this.id)).background();

            label.margin.top(20);
            elements.add(label);

            for (Value value : this.getSubValues())
            {
                GuiElement element = new GuiElement(mc);

                element.flex().row(0).preferred(0).height(20);
                element.add(Elements.label(KeyParser.fromJson(value.id), 0).anchor(0, 0.5F));

                GuiKeybindElement keybind = new GuiKeybindElement(mc, (ValueInt) value);

                keybind.flex().w(90);
                element.add(keybind.removeTooltip());

                elements.add(element);
            }

            return elements;
        }
    }
}
