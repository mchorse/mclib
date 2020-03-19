package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;

public class GuiTrackpadElement extends GuiElement implements IFocusedGuiElement
{
    public Consumer<Float> callback;
    public GuiTextField text;

    public String title = "";
    public float value;
    public float amplitude = 0.25F;
    public float min = Float.NEGATIVE_INFINITY;
    public float max = Float.POSITIVE_INFINITY;
    public boolean integer;

    /* Value dragging fields */
    private boolean dragging;
    private int lastX;
    private int lastY;
    private float lastValue;

    public GuiTrackpadElement(Minecraft mc, String label, Consumer<Float> callback)
    {
        super(mc);

        this.callback = callback;
        this.setTitle(label);

        this.text = new GuiTextField(0, font, 0, 0, 0, 0);
        this.text.setEnableBackgroundDrawing(false);
    }

    /**
     * Set the title of this trackpad
     */
    public GuiTrackpadElement setTitle(String title)
    {
        this.title = title;

        return this;
    }

    public void setLimit(float min, float max)
    {
        this.min = min;
        this.max = max;
    }

    public void setLimit(float min, float max, boolean integer)
    {
        this.setLimit(min, max);
        this.integer = integer;
    }

    /**
     * Whether this trackpad is dragging
     */
    public boolean isDragging()
    {
        return this.dragging;
    }

    /**
     * Set the value of the field. The input value would be rounded up to 3
     * decimal places.
     */
    public void setValue(float value)
    {
        value = Math.round(value * 1000F) / 1000F;
        value = MathHelper.clamp_float(value, this.min, this.max);

        if (this.integer)
        {
            value = (int) value;
        }

        this.value = value;
        this.text.setText(this.integer ? String.valueOf((int) value) : String.valueOf(value));
        this.text.setCursorPositionZero();
    }

    /**
     * Set value of this field and also notify the trackpad listener so it
     * could detect the value change.
     */
    public void setValueAndNotify(float value)
    {
        this.setValue(value);

        if (this.callback != null)
        {
            this.callback.accept(value);
        }
    }

    /**
     * Update the bounding box of this GUI field
     */
    @Override
    public void resize()
    {
        super.resize();

        this.text.setCursorPositionZero();
    }

    /**
     * Delegates mouse click to text field and initiate value dragging if the
     * cursor inside of trackpad's bounding box.
     */
    @Override
    public boolean mouseClicked(GuiContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        this.text.mouseClicked(mouseX, mouseY, context.mouseButton);

        if (!this.text.isFocused() && this.area.isInside(mouseX, mouseY))
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
            {
                this.setValueAndNotify(Math.round(this.value));
            }

            this.dragging = true;
            this.lastX = mouseX;
            this.lastY = mouseY;
            this.lastValue = this.value;
        }

        return this.isDragging() || super.mouseClicked(context);
    }

    /**
     * Reset value dragging
     */
    @Override
    public void mouseReleased(GuiContext context)
    {
        this.dragging = false;

        super.mouseReleased(context);
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        String old = this.text.getText();
        boolean result = this.text.textboxKeyTyped(context.typedChar, context.keyCode);
        String text = this.text.getText();

        if (this.text.isFocused() && !text.equals(old))
        {
            try
            {
                this.value = text.isEmpty() ? 0 : Float.parseFloat(text);

                if (this.callback != null)
                {
                    this.callback.accept(value);
                }
            }
            catch (Exception e)
            {}
        }

        return result || super.keyTyped(context);
    }

    @Override
    public boolean isFocused()
    {
        return this.text.isFocused();
    }

    @Override
    public void focus(GuiContext context)
    {
        this.text.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void unfocus(GuiContext context)
    {
        this.text.setFocused(false);
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Draw the trackpad
     *
     * This method will not only draw the text box, background and title label,
     * but also dragging the numerical value based on the mouse input.
     */
    @Override
    public void draw(GuiContext context)
    {
        int x = this.area.x;
        int y = this.area.y;
        int w = this.area.w;
        int h = this.area.h;

        Gui.drawRect(x, y, x + w, y + h, 0xff000000);
        GlStateManager.color(1, 1, 1, 1);
        Icons.MOVE_LEFT.render(x + 4, y + (h - 16) / 2);
        Icons.MOVE_RIGHT.render(x + w - 12, y + (h - 16) / 2);

        int width = MathUtils.clamp(this.font.getStringWidth(this.text.getText()), 0, w - 16);

        this.text.xPosition = this.area.getX(0.5F, -width);
        this.text.yPosition = this.area.getY(0.5F) - 4;
        this.text.width = width + 6;
        this.text.height = 9;
        this.text.drawTextBox();

        if (this.dragging)
        {
            int dx = context.mouseX - this.lastX;
            int dy = context.mouseY - this.lastY;

            if (dx != 0 || dy != 0)
            {
                float amp = 1.0F;

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                {
                    amp = 5.0F;
                }
                else if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
                {
                    amp = 0.2F;
                }

                float diff = ((int) Math.sqrt(dx * dx + dy * dy) - 3) * this.amplitude * amp;
                float newValue = this.lastValue + (dx < 0 ? -diff : diff);

                newValue = diff < 0 ? this.lastValue : Math.round(newValue * 1000F) / 1000F;

                if (this.value != newValue)
                {
                    this.setValueAndNotify(MathHelper.clamp_float(newValue, this.min, this.max));
                }
            }

            Gui.drawRect(this.lastX - 4, this.lastY - 4, this.lastX - 3, this.lastY + 4, 0xffffffff);
            Gui.drawRect(this.lastX + 3, this.lastY - 4, this.lastX + 4, this.lastY + 4, 0xffffffff);
            Gui.drawRect(this.lastX - 3, this.lastY - 4, this.lastX + 3, this.lastY - 3, 0xffffffff);
            Gui.drawRect(this.lastX - 3, this.lastY + 3, this.lastX + 3, this.lastY + 4, 0xffffffff);
        }

        super.draw(context);
    }
}