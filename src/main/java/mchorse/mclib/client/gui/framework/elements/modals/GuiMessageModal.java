package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiMessageModal extends GuiModal
{
    public GuiButtonElement button;

    public GuiMessageModal(Minecraft mc, String label)
    {
        super(mc, label);

        this.button = new GuiButtonElement(mc, I18n.format("mclib.gui.ok"), (b) -> this.removeFromParent());
        this.button.resizer().parent(this.area).set(10, 0, 0, 20).y(1, -30).w(1, -20);

        this.add(this.button);
    }
}