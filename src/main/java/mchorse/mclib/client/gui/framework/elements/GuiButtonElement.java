package mchorse.mclib.client.gui.framework.elements;

import java.util.function.Consumer;

import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiButtonElement<T extends GuiButton> extends GuiElement
{
    public T button;
    public Consumer<GuiButtonElement<T>> callback;

    public static GuiButtonElement<GuiCheckBox> checkbox(Minecraft mc, String label, boolean value, Consumer<GuiButtonElement<GuiCheckBox>> callback)
    {
        return new GuiButtonElement<GuiCheckBox>(mc, new GuiCheckBox(0, 0, 0, label, value), callback);
    }

    public static GuiButtonElement<GuiTextureButton> icon(Minecraft mc, Icon active, Icon hovered, Consumer<GuiButtonElement<GuiTextureButton>> callback)
    {
        return new GuiButtonElement<GuiTextureButton>(mc, new GuiTextureButton(0, 0, 0, active).setHovered(hovered), callback);
    }

    public static GuiButtonElement<GuiTextureButton> icon(Minecraft mc, Icon icon, Consumer<GuiButtonElement<GuiTextureButton>> callback)
    {
        return icon(mc, icon, icon, callback);
    }

    public static GuiButtonElement<GuiButton> button(Minecraft mc, String label, Consumer<GuiButtonElement<GuiButton>> callback)
    {
        return new GuiButtonElement<GuiButton>(mc, new GuiButton(0, 0, 0, label), callback);
    }

    public GuiButtonElement(Minecraft mc, T button, Consumer<GuiButtonElement<T>> callback)
    {
        super(mc);
        this.button = button;
        this.callback = callback;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        this.button.enabled = enabled;
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        this.button.visible = visible;
    }

    @Override
    public void resize()
    {
        super.resize();

        this.button.xPosition = this.area.x;
        this.button.yPosition = this.area.y;
        this.button.width = this.area.w;
        this.button.height = this.area.h;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (this.button.mousePressed(this.mc, context.mouseX, context.mouseY))
        {
            this.button.playPressSound(this.mc.getSoundHandler());

            if (this.callback != null)
            {
                this.callback.accept(this);
            }

            return true;
        }

        return false;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        this.button.mouseReleased(context.mouseX, context.mouseY);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.button.drawButton(this.mc, context.mouseX, context.mouseY);

        super.draw(context);
    }
}