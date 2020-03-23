package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.function.Consumer;

public class GuiPromptModal extends GuiModal
{
    public Consumer<String> callback;

    public GuiTextElement text;
    public GuiButtonElement confirm;
    public GuiButtonElement cancel;

    public GuiPromptModal(Minecraft mc, String label, Consumer<String> callback)
    {
        super(mc, label);

        this.callback = callback;
        this.text = new GuiTextElement(mc, null);
        this.text.resizer().parent(this.area).set(10, 0, 0, 20).y(1, -55).w(1, -20);
        this.text.field.setFocused(true);

        this.confirm = new GuiButtonElement(mc, I18n.format("mclib.gui.ok"), (b) -> this.send());
        this.confirm.resizer().parent(this.area).set(10, 0, 0, 20).y(1, -30).w(0.5F, -15);

        this.cancel = new GuiButtonElement(mc, I18n.format("mclib.gui.cancel"), (b) -> this.removeFromParent());
        this.cancel.resizer().parent(this.area).set(10, 0, 0, 20).x(0.5F, 5).y(1, -30).w(0.5F, -15);

        this.add(this.text, this.confirm, this.cancel);
    }

    public GuiPromptModal setValue(String value)
    {
        this.text.setText(value);

        return this;
    }

    private void send()
    {
        String text = this.text.field.getText();

        if (!text.isEmpty())
        {
            this.removeFromParent();

            if (this.callback != null)
            {
                this.callback.accept(text);
            }
        }
    }
}