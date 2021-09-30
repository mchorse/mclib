package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IFocusedGuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.TextField;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public abstract class GuiBaseTextElement extends GuiElement implements IFocusedGuiElement
{
    public TextField field;

    public GuiBaseTextElement(Minecraft mc)
    {
        super(mc);

        this.field = new TextField(this.font, this::userText);
    }

    protected void userText(String text)
    {}

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        this.field.setEnabled(enabled);
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        this.field.setVisible(visible);
    }

    @Override
    public boolean isFocused()
    {
        return this.field.isFocused();
    }

    @Override
    public void focus(GuiContext context)
    {
        this.field.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void unfocus(GuiContext context)
    {
        this.field.setFocused(false);
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void selectAll(GuiContext context)
    {
        this.field.moveCursorToStart();
        this.field.setSelection(this.field.getText().length());
    }

    @Override
    public void unselect(GuiContext context)
    {
        this.field.clearSelection();
    }
}