package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

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

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        if (context.keyCode == Keyboard.KEY_RETURN)
        {
            this.removeFromParent();

            return true;
        }

        return false;
    }
}