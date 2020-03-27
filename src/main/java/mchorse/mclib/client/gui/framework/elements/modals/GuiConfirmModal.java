package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.function.Consumer;

public class GuiConfirmModal extends GuiModal
{
    public GuiButtonElement confirm;
    public GuiButtonElement cancel;

    public Consumer<Boolean> callback;

    public GuiConfirmModal(Minecraft mc, String label, Consumer<Boolean> callback)
    {
        super(mc, label);

        this.callback = callback;
        this.confirm = new GuiButtonElement(mc, I18n.format("mclib.gui.ok"), (b) -> this.close(true));
        this.confirm.flex().parent(this.area).set(10, 0, 0, 20).y(1, -30).w(0.5F, -15);

        this.cancel = new GuiButtonElement(mc, I18n.format("mclib.gui.cancel"), (b) -> this.close(false));
        this.cancel.flex().parent(this.area).set(10, 0, 0, 20).x(0.5F, 5).y(1, -30).w(0.5F, -15);

        this.add(this.confirm, this.cancel);
    }

    public void close(boolean confirmed)
    {
        if (this.callback != null)
        {
            this.callback.accept(confirmed);
        }

        this.removeFromParent();
    }
}