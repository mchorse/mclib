package mchorse.mclib.client.gui.framework.elements.modals;

import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiConfirmModal extends GuiModal
{
    public GuiButtonElement<GuiButton> confirm;
    public GuiButtonElement<GuiButton> cancel;

    public Consumer<Boolean> callback;

    public GuiConfirmModal(Minecraft mc, GuiDelegateElement<IGuiElement> parent, String label, Consumer<Boolean> callback)
    {
        super(mc, parent, label);

        this.callback = callback;
        this.confirm = GuiButtonElement.button(mc, I18n.format("mclib.gui.ok"), (b) -> this.close(true));
        this.confirm.resizer().parent(this.area).set(10, 0, 0, 20).y(1, -30).w(0.5F, -15);

        this.cancel = GuiButtonElement.button(mc, I18n.format("mclib.gui.cancel"), (b) -> this.close(false));
        this.cancel.resizer().parent(this.area).set(10, 0, 0, 20).x(0.5F, 5).y(1, -30).w(0.5F, -15);

        this.add(this.confirm, this.cancel);
    }

    public void close(boolean confirmed)
    {
        if (this.callback != null)
        {
            this.callback.accept(confirmed);
        }

        this.parent.setDelegate(null);
    }
}