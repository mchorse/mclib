package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;

public class GuiConfirmModal extends GuiModal
{
    public GuiButtonElement confirm;
    public GuiButtonElement cancel;

    public Consumer<Boolean> callback;

    public GuiConfirmModal(Minecraft mc, IKey label, Consumer<Boolean> callback)
    {
        super(mc, label);

        this.callback = callback;

        this.confirm = new GuiButtonElement(mc, IKey.lang("mclib.gui.ok"), (b) -> this.close(true));
        this.cancel = new GuiButtonElement(mc, IKey.lang("mclib.gui.cancel"), (b) -> this.close(false));

        this.bar.add(this.confirm, this.cancel);
    }

    public void close(boolean confirmed)
    {
        if (this.callback != null)
        {
            this.callback.accept(confirmed);
        }

        this.removeFromParent();
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        if (context.keyCode == Keyboard.KEY_RETURN || context.keyCode == Keyboard.KEY_ESCAPE)
        {
            (context.keyCode == Keyboard.KEY_RETURN ? this.confirm : this.cancel).clickItself(context);

            return true;
        }

        return false;
    }
}