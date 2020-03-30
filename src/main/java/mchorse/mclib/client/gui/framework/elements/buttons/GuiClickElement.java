package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.util.function.Consumer;

public abstract class GuiClickElement<T extends GuiClickElement> extends GuiElement
{
    public Consumer<T> callback;

    protected boolean hover;
    protected boolean pressed;

    public GuiClickElement(Minecraft mc, Consumer<T> callback)
    {
        super(mc);

        this.callback = callback;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (context.mouseButton == 0 && this.area.isInside(context))
        {
            this.pressed = true;
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.click();

            return true;
        }

        return false;
    }

    protected void click()
    {
        if (this.callback != null)
        {
            this.callback.accept((T) this);
        }
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);
        this.pressed = false;
    }

    @Override
    public void draw(GuiContext context)
    {
        this.hover = this.area.isInside(context);

        this.drawSkin(context);
        super.draw(context);
    }

    protected abstract void drawSkin(GuiContext context);
}