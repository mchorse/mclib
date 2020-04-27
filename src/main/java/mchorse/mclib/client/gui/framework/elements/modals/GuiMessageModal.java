package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiMessageModal extends GuiModal
{
    public GuiButtonElement button;

    public GuiMessageModal(Minecraft mc, IKey label)
    {
        super(mc, label);

        this.button = new GuiButtonElement(mc, IKey.lang("mclib.gui.ok"), (b) -> this.removeFromParent());
        this.button.flex().relative(this).set(10, 0, 0, 20).y(1, -30).w(1, -20);

        this.add(this.button);
    }
}