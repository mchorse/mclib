package mchorse.mclib.client.gui.framework.elements;

import java.util.function.Consumer;

import mchorse.mclib.client.gui.widgets.GuiTrackpad;
import mchorse.mclib.client.gui.widgets.GuiTrackpad.ITrackpadListener;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class GuiTrackpadElement extends GuiElement implements ITrackpadListener, IFocusedGuiElement
{
    public GuiTrackpad trackpad;
    public Consumer<Float> callback;

    public GuiTrackpadElement(Minecraft mc, String label, Consumer<Float> callback)
    {
        super(mc);

        this.trackpad = new GuiTrackpad(this, this.font);
        this.trackpad.setTitle(label);
        this.callback = callback;
    }

    @Override
    public void setTrackpadValue(GuiTrackpad trackpad, float value)
    {
        if (this.callback != null)
        {
            this.callback.accept(value);
        }
    }

    public void setLimit(float min, float max)
    {
        this.trackpad.min = min;
        this.trackpad.max = max;
    }

    public void setLimit(float min, float max, boolean integer)
    {
        this.setLimit(min, max);
        this.trackpad.integer = integer;
    }

    public void setValue(float value)
    {
        this.trackpad.setValue(value);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.trackpad.update(this.area.x, this.area.y, this.area.w, this.area.h);
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        this.trackpad.mouseClicked(context.mouseX, context.mouseY, context.mouseButton);

        return this.trackpad.isDragging();
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        this.trackpad.mouseReleased(context.mouseX, context.mouseY, context.mouseButton);
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        return super.keyTyped(context) || this.trackpad.keyTyped(context.typedChar, context.keyCode);
    }

    @Override
    public boolean isFocused()
    {
        return this.trackpad.text.isFocused();
    }

    @Override
    public void focus(GuiContext context)
    {
        this.trackpad.text.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void unfocus(GuiContext context)
    {
        this.trackpad.text.setFocused(false);
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.trackpad.draw(context.mouseX, context.mouseY, context.partialTicks);

        super.draw(context);
    }
}