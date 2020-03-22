package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.GuiDelegateElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiMessageModal extends GuiModal
{
    public GuiButtonElement button;

    public GuiMessageModal(Minecraft mc, GuiDelegateElement<GuiElement> parent, String label)
    {
        super(mc, parent, label);

        this.button = new GuiButtonElement(mc, I18n.format("mclib.gui.ok"), (b) -> parent.setDelegate(null));
        this.button.resizer().parent(this.area).set(10, 0, 0, 20).y(1, -30).w(1, -20);

        this.add(this.button);
    }
}