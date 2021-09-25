package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.ITextColoring;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiButtonElement extends GuiClickElement<GuiButtonElement> implements ITextColoring
{
    public IKey label;

    public int textColor = 0xffffff;
    public boolean textShadow = true;

    public boolean custom;
    public int customColor;
    public boolean background = true;

    public GuiButtonElement(Minecraft mc, IKey label, Consumer<GuiButtonElement> callback)
    {
        super(mc, callback);

        this.label = label;
        this.flex().h(20);
    }

    public GuiButtonElement color(int color)
    {
        this.custom = true;
        this.customColor = color & 0xffffff;

        return this;
    }

    public GuiButtonElement textColor(int color, boolean shadow)
    {
        this.textColor = color;
        this.textShadow = shadow;

        return this;
    }

    public GuiButtonElement background(boolean background)
    {
        this.background = background;

        return this;
    }

    @Override
    public void setColor(int color, boolean shadow)
    {
        this.textColor = color;
        this.textShadow = shadow;
    }

    @Override
    protected GuiButtonElement get()
    {
        return this;
    }

    @Override
    protected void drawSkin(GuiContext context)
    {
        int color = 0xff000000 + (this.custom ? this.customColor : McLib.primaryColor.get());

        if (this.hover)
        {
            color = ColorUtils.multiplyColor(color, 0.85F);
        }

        if (this.background)
        {
            GuiDraw.drawBorder(this.area, color);
        }

        String label = this.label.get();
        int x = this.area.mx(this.font.getStringWidth(label));
        int y = this.area.my(this.font.FONT_HEIGHT - 1);

        this.font.drawString(label, x, y, ColorUtils.multiplyColor(this.textColor, this.hover ? 0.9F : 1F), this.textShadow);

        GuiDraw.drawLockedArea(this);
    }
}