package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class GuiListModal extends GuiModal
{
    public Consumer<List<String>> callback;
    public String label;

    public GuiButtonElement pick;
    public GuiButtonElement cancel;
    public GuiStringListElement list;

    public GuiListModal(Minecraft mc, IKey label, Consumer<String> callback)
    {
        super(mc, label);

        this.callback = (list) ->
        {
            if (callback != null)
            {
                callback.accept(this.list.getIndex() == 0 ? "" : this.list.getCurrentFirst());
            }
        };

        this.pick = new GuiButtonElement(mc, IKey.lang("mclib.gui.ok"), (b) -> this.send());
        this.cancel = new GuiButtonElement(mc, IKey.lang("mclib.gui.cancel"), (b) -> this.removeFromParent());
        this.list = new GuiStringListElement(mc, null);

        this.list.flex().set(10, 0, 0, 0).relative(this.area).y(0.4F, 0).w(1, -20).h(0.6F, -35);
        this.list.add(I18n.format("mclib.gui.none"));
        this.list.setIndex(0);

        this.bar.add(this.pick, this.cancel);
        this.add(this.list);
    }

    public GuiListModal callback(Consumer<List<String>> callback)
    {
        this.callback = callback;

        return this;
    }

    public GuiListModal setValue(String value)
    {
        if (value.isEmpty())
        {
            this.list.setIndex(0);
        }
        else
        {
            this.list.setCurrent(value);
        }

        return this;
    }

    public GuiListModal addValues(Collection<String> values)
    {
        this.list.add(values);

        return this;
    }

    public void send()
    {
        if (this.list.isDeselected())
        {
            return;
        }

        this.removeFromParent();

        if (this.callback != null)
        {
            this.callback.accept(this.list.getCurrent());
        }
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        if (context.keyCode == Keyboard.KEY_RETURN)
        {
            this.send();

            return true;
        }
        else if (context.keyCode == Keyboard.KEY_ESCAPE)
        {
            this.removeFromParent();

            return true;
        }

        return false;
    }
}