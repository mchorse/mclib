package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.values.ValueDouble;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueLong;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import mchorse.mclib.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.function.Consumer;

public class GuiTrackpadElement extends GuiBaseTextElement
{
    public static final DecimalFormat FORMAT;

    public Consumer<Double> callback;

    public double value;

    /* Trackpad options */
    public double strong = 1D;
    public double normal = 0.25D;
    public double weak = 0.05D;
    public double increment = 1;
    public double min = Double.NEGATIVE_INFINITY;
    public double max = Double.POSITIVE_INFINITY;
    public boolean integer;

    /* Value dragging fields */
    private boolean dragging;
    private int shiftX;
    private int initialX;
    private int initialY;
    private double lastValue;
    private double valueBeforeDrag;

    private Timer changed = new Timer(30);

    private long time;
    private Area plusOne = new Area();
    private Area minusOne = new Area();
    private int decimalPlaces;
    private DecimalFormat rounding;

    static
    {
        FORMAT = new DecimalFormat("#.###");
        FORMAT.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    {
        this.decimalPlaces = McLib.trackpadDecimalPlaces.get();

        this.getRoundingFormat(true);
    }

    public GuiTrackpadElement(Minecraft mc, ValueInt value)
    {
        this(mc, value, null);
    }

    public GuiTrackpadElement(Minecraft mc, ValueInt value, @Nullable Consumer<Double> callback)
    {
        this(mc, callback == null ? (v) -> value.set(v.intValue()) : (v) ->
        {
            value.set(v.intValue());
            callback.accept(v);
        });
        this.limit(value.getMin(), value.getMax(), true);
        this.setValue(value.get());
        this.tooltip(IKey.lang(value.getCommentKey()));
    }

    public GuiTrackpadElement(Minecraft mc, ValueLong value)
    {
        this(mc, value, null);
    }

    public GuiTrackpadElement(Minecraft mc, ValueLong value, @Nullable Consumer<Double> callback)
    {
        this(mc, callback == null ? (v) -> value.set(v.longValue()) : (v) ->
        {
            value.set(v.longValue());
            callback.accept(v);
        });
        this.limit(value.getMin(), value.getMax(), true);
        this.setValue(value.get());
        this.tooltip(IKey.lang(value.getCommentKey()));
    }

    public GuiTrackpadElement(Minecraft mc, ValueFloat value)
    {
        this(mc, value, null);
    }

    public GuiTrackpadElement(Minecraft mc, ValueFloat value, @Nullable Consumer<Double> callback)
    {
        this(mc, callback == null ? (v) -> value.set(v.floatValue()) : (v) ->
        {
            value.set(v.floatValue());
            callback.accept(v);
        });
        this.limit(value.getMin(), value.getMax());
        this.setValue(value.get());
        this.tooltip(IKey.lang(value.getCommentKey()));
    }

    public GuiTrackpadElement(Minecraft mc, ValueDouble value)
    {
        this(mc, value, null);
    }

    public GuiTrackpadElement(Minecraft mc, ValueDouble value, @Nullable Consumer<Double> callback)
    {
        this(mc, callback == null ? value::set : (v) ->
        {
            value.set(v);
            callback.accept(v);
        });
        this.limit(value.getMin(), value.getMax());
        this.setValue(value.get());
        this.tooltip(IKey.lang(value.getCommentKey()));
    }

    public GuiTrackpadElement(Minecraft mc, @Nullable Consumer<Double> callback)
    {
        super(mc);

        this.callback = callback;

        this.field.setEnableBackgroundDrawing(false);
        this.setValue(0);

        this.flex().h(20);
    }

    public GuiTrackpadElement max(double max)
    {
        this.max = max;

        return this;
    }

    public GuiTrackpadElement limit(double min)
    {
        this.min = min;

        return this;
    }

    public GuiTrackpadElement limit(double min, double max)
    {
        this.min = min;
        this.max = max;

        return this;
    }

    public GuiTrackpadElement limit(double min, double max, boolean integer)
    {
        this.integer = integer;

        return this.limit(min, max);
    }

    public GuiTrackpadElement integer()
    {
        this.integer = true;

        return this;
    }

    public GuiTrackpadElement increment(double increment)
    {
        this.increment = increment;

        return this;
    }

    public GuiTrackpadElement values(double normal)
    {
        this.normal = normal;
        this.weak = normal / 5D;
        this.strong = normal * 5D;

        return this;
    }

    public GuiTrackpadElement values(double normal, double weak, double strong)
    {
        this.normal = normal;
        this.weak = weak;
        this.strong = strong;

        return this;
    }

    /* Values presets */

    public GuiTrackpadElement degrees()
    {
        return this.increment(15D).values(1D, 0.1D, 5D);
    }

    public GuiTrackpadElement block()
    {
        return this.increment(1 / 16D).values(1 / 32D, 1 / 128D, 1 / 2D);
    }

    public GuiTrackpadElement metric()
    {
        return this.values(0.1D, 0.01D, 1);
    }

    /**
     * Whether this trackpad is dragging
     */
    public boolean isDragging()
    {
        return this.dragging;
    }

    public boolean isDraggingTime()
    {
        return this.isDragging() && System.currentTimeMillis() - this.time > 150;
    }

    /**
     * Set the value of the field. The input value would be rounded up to 3
     * decimal places.
     */
    public void setValue(double value)
    {
        this.setValueInternal(value);
        this.field.setText(this.integer ? String.valueOf((int) this.value) : FORMAT.format(this.value));
        this.field.setCursorPositionZero();
    }

    private void setValueInternal(double value)
    {
        value = Double.valueOf(this.getRoundingFormat().format(value));
        value = MathUtils.clamp(value, this.min, this.max);

        if (this.integer)
        {
            value = (int) value;
        }

        this.value = value;
    }

    /**
     * Set value of this field and also notify the trackpad listener so it
     * could detect the value change.
     */
    public void setValueAndNotify(double value)
    {
        this.setValue(value);

        if (this.callback != null)
        {
            this.callback.accept(this.value);
        }
    }

    @Override
    public void unfocus(GuiContext context)
    {
        super.unfocus(context);

        /* Reset the value in case it's out of range */
        this.setValue(this.value);

        this.field.setText(this.integer ? String.valueOf((int) this.value) : FORMAT.format(this.value));
    }

    @Override
    public void focus(GuiContext context)
    {
        super.focus(context);

        int valueint = (int) this.value;

        this.field.setText(this.integer ? String.valueOf(valueint) : (this.value % 1 == 0) ? String.valueOf(valueint) : String.valueOf(this.value));
        this.field.setCursorPositionZero();
    }

    /**
     * Update the bounding box of this GUI field
     */
    @Override
    public void resize()
    {
        super.resize();

        this.plusOne.copy(this.area);
        this.minusOne.copy(this.area);
        this.plusOne.w = this.minusOne.w = 20;
        this.plusOne.x = this.area.ex() - 20;
    }

    /**
     * Delegates mouse click to text field and initiate value dragging if the
     * cursor inside of trackpad's bounding box.
     */
    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (context.mouseButton == 0)
        {
            boolean wasFocused = this.field.isFocused();

            this.field.mouseClicked(context.mouseX, context.mouseY, context.mouseButton);

            if (wasFocused != this.field.isFocused())
            {
                context.focus(wasFocused ? null : this);
            }

            if (this.area.isInside(context))
            {
                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                {
                    this.setValueAndNotify(Math.round(this.value));

                    return true;
                }

                if (!this.field.isFocused() && !(this.plusOne.isInside(context) || this.minusOne.isInside(context)))
                {
                    context.focus(this);
                }

                if (!this.dragging)
                {
                    valueBeforeDrag = this.value;
                }

                this.dragging = true;
                this.initialX = context.mouseX;
                this.initialY = context.mouseY;
                this.lastValue = this.value;
                this.time = System.currentTimeMillis();
                this.allowContextMenu = false;
                globalAllowContextMenu = false;
            }
        }

        /* release dragging and reset to what was before dragging, when right clicking */
        if (context.mouseButton == 1 && this.dragging)
        {
            this.setValueAndNotify(this.valueBeforeDrag);

            this.stopDragging();
        }

        return context.mouseButton == 0 && this.area.isInside(context);
    }

    /**
     * Reset value dragging
     */
    @Override
    public void mouseReleased(GuiContext context)
    {
        if (this.dragging && !this.isDraggingTime() && context.mouseButton == 0 && McLib.enableTrackpadIncrements.get())
        {
            if (this.plusOne.isInside(context))
            {
                this.setValueAndNotify(this.value + this.increment);
            }
            else if (this.minusOne.isInside(context))
            {
                this.setValueAndNotify(this.value - this.increment);
            }
        }

        /* only allow context menu when right click is released
        * (which means before this, right click was used to abort the dragging process)
        * or if this.dragging == true, which means dragging process was not aborted through right click */
        if (context.mouseButton == 1 || this.dragging)
        {
            this.allowContextMenu = true;
            globalAllowContextMenu = true;
        }

        this.stopDragging();

        super.mouseReleased(context);
    }

    protected void stopDragging()
    {
        this.dragging = false;
        this.shiftX = 0;
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        if (this.isFocused())
        {
            if (context.keyCode == Keyboard.KEY_UP)
            {
                this.setValueAndNotify(this.value + this.getValueModifier());

                return true;
            }
            else if (context.keyCode == Keyboard.KEY_DOWN)
            {
                this.setValueAndNotify(this.value - this.getValueModifier());

                return true;
            }
            else if (context.keyCode == Keyboard.KEY_TAB)
            {
                context.focus(this, -1, GuiScreen.isShiftKeyDown() ? -1 : 1);

                return true;
            }
            else if (context.keyCode == Keyboard.KEY_ESCAPE)
            {
                context.unfocus();

                return true;
            }
            else if (context.keyCode == Keyboard.KEY_RETURN && GuiScreen.isAltKeyDown())
            {
                try
                {
                    MathBuilder builder = new MathBuilder();

                    this.setValueAndNotify(builder.parse(this.field.getText()).get().doubleValue());
                }
                catch (Exception e)
                {}
            }
        }

        String old = this.field.getText();
        boolean result = this.field.textboxKeyTyped(context.typedChar, context.keyCode);
        String text = this.field.getText();

        if (this.field.isFocused() && !text.equals(old))
        {
            try
            {
                this.setValueInternal(text.isEmpty() ? 0 : Double.parseDouble(text));

                if (this.callback != null)
                {
                    this.callback.accept(this.value);
                }
            }
            catch (Exception e)
            {}
        }

        return result;
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
        int padding = McLib.enableBorders.get() ? 1 : 0;

        this.area.draw(0xff000000);

        boolean dragging = this.isDraggingTime();
        boolean plus = !dragging && this.plusOne.isInside(context);
        boolean minus = !dragging && this.minusOne.isInside(context);

        if (dragging)
        {
            /* Draw filling background */
            int color = McLib.primaryColor.get();
            int fx = MathUtils.clamp(context.mouseX, this.area.x + padding, this.area.ex() - padding);

            Gui.drawRect(Math.min(fx, this.initialX), this.area.y + padding, Math.max(fx, this.initialX), this.area.ey() - padding, 0xff000000 + color);
        }

        if (McLib.enableTrackpadIncrements.get())
        {
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
            this.plusOne.draw(plus ? 0x22ffffff : 0x0affffff, padding);
            this.minusOne.draw(minus ? 0x22ffffff : 0x0affffff, padding);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

            GlStateManager.enableBlend();
            ColorUtils.bindColor(minus ? 0xffffffff : 0x80ffffff);
            Icons.MOVE_LEFT.render(x + 5, y + (h - 16) / 2);
            ColorUtils.bindColor(plus ? 0xffffffff : 0x80ffffff);
            Icons.MOVE_RIGHT.render(x + w - 13, y + (h - 16) / 2);
            GlStateManager.disableBlend();
        }

        int width = MathUtils.clamp(this.font.getStringWidth(this.field.getText()), 0, w - 16);

        this.field.x = this.area.mx(width);
        this.field.y = this.area.my() - 4;
        this.field.width = width + 6;
        this.field.height = 9;
        this.field.drawTextBox();

        if (dragging)
        {
            double factor = Math.ceil(this.mc.displayWidth / (double) context.screen.width);
            int mouseX = context.globalX(context.mouseX);

            /* Mouse doesn't change immediately the next frame after Mouse.setCursorPosition(),
             * so this is a hack that stops for double shifting */
            if (this.changed.isTime())
            {
                final int border = 5;
                final int borderPadding = border + 1;
                boolean stop = false;

                if (mouseX <= border)
                {
                    Mouse.setCursorPosition(this.mc.displayWidth - (int) (factor * borderPadding), Mouse.getY());

                    this.shiftX -= context.screen.width - borderPadding * 2;
                    this.changed.mark();
                    stop = true;
                }
                else if (mouseX >= context.screen.width - border)
                {
                    Mouse.setCursorPosition((int) (factor * borderPadding), Mouse.getY());

                    this.shiftX += context.screen.width - borderPadding * 2;
                    this.changed.mark();
                    stop = true;
                }

                if (!stop)
                {
                    if (this.isFocused())
                    {
                        context.unfocus();
                    }

                    int dx = (this.shiftX + context.mouseX) - this.initialX;

                    if (dx != 0)
                    {
                        double value = this.getValueModifier();

                        double diff = (Math.abs(dx) - 3) * value;
                        double newValue = this.lastValue + (dx < 0 ? -diff : diff);

                        newValue = diff < 0 ? this.lastValue : Double.valueOf(this.getRoundingFormat().format(newValue));;

                        if (this.value != newValue)
                        {
                            this.setValueAndNotify(MathUtils.clamp(newValue, this.min, this.max));
                        }
                    }
                }
            }

            /* Draw active element */
            GuiDraw.drawOutlineCenter(this.initialX, this.initialY, 4, 0xffffffff);
        }

        GuiDraw.drawLockedArea(this);

        super.draw(context);
    }

    protected double getValueModifier()
    {
        double value = this.normal;

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
        {
            value = this.strong;
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
        {
            value = this.increment;
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
        {
            value = this.weak;
        }

        return value;
    }

    public DecimalFormat getRoundingFormat()
    {
        return this.getRoundingFormat(false);
    }

    /**
     * Get the rounding format according to McLib's configuration of trackpadDecimalPlaces
     * @param force true to recalculate the rounding format forcefully
     * @return the rounding format
     */
    private DecimalFormat getRoundingFormat(boolean force)
    {
        if (this.decimalPlaces != McLib.trackpadDecimalPlaces.get() || force) //to save performance
        {
            String decimals = "";

            for(int i = 0; i < McLib.trackpadDecimalPlaces.get(); i++)
            {
                decimals += "#";
            }

            this.rounding = new DecimalFormat("#."+decimals);
            this.decimalPlaces = McLib.trackpadDecimalPlaces.get();

            this.rounding.setRoundingMode(RoundingMode.HALF_EVEN);
        }

        return this.rounding;
    }
}