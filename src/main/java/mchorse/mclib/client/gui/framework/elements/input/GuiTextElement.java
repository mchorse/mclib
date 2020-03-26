package mchorse.mclib.client.gui.framework.elements.input;

import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IFocusedGuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

/**
 * GUI text element
 * 
 * This element is a wrapper for the text field class
 */
public class GuiTextElement extends GuiElement implements GuiResponder, IFocusedGuiElement
{
    public GuiTextField field;
    public Consumer<String> callback;

    public GuiTextElement(Minecraft mc, int maxLength, Consumer<String> callback)
    {
        this(mc, callback);
        this.field.setMaxStringLength(maxLength);
    }

    public GuiTextElement(Minecraft mc, Consumer<String> callback)
    {
        super(mc);

        this.field = new GuiTextField(0, this.font, 0, 0, 0, 0);
        this.field.setGuiResponder(this);
        this.callback = callback;
    }

    public void setText(String text)
    {
        if (text == null)
        {
            text = "";
        }

        this.field.setText(text);
        this.field.setCursorPositionZero();
    }

    @Override
    public void setEntryValue(int id, boolean value)
    {}

    @Override
    public void setEntryValue(int id, float value)
    {}

    @Override
    public void setEntryValue(int id, String value)
    {
        if (this.callback != null)
        {
            this.callback.accept(value);
        }
    }

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
    public void resize()
    {
        super.resize();

        this.field.x = this.area.x + 1;
        this.field.y = this.area.y + 1;
        this.field.width = this.area.w - 2;
        this.field.height = this.area.h - 2;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        boolean wasFocused = this.field.isFocused();

        this.field.mouseClicked(context.mouseX, context.mouseY, context.mouseButton);

        if (wasFocused != this.field.isFocused())
        {
            context.focus(wasFocused ? null : this);
        }

        return false;
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
    public boolean keyTyped(GuiContext context)
    {
        if (this.isFocused() && context.keyCode == Keyboard.KEY_TAB)
        {
            context.focus(this, -1, GuiScreen.isShiftKeyDown() ? -1 : 1);

            return true;
        }

        return this.field.textboxKeyTyped(context.typedChar, context.keyCode) || super.keyTyped(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.field.drawTextBox();

        GuiDraw.drawLockedArea(this);

        super.draw(context);
    }
}