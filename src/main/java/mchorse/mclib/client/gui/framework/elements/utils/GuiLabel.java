package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

public class GuiLabel extends GuiElement implements ITextColoring
{
    public IKey label;
    public int color;
    public float anchorX;
    public float anchorY;
    public int background;
    public Supplier<Integer> backgroundColor;

    public GuiLabel(Minecraft mc, IKey label)
    {
        this(mc, label, 0xffffff);
    }

    public GuiLabel(Minecraft mc, IKey label, int color)
    {
        super(mc);

        this.label = label;
        this.color = color;
    }

    @Override
    public void setColor(int color)
    {
        this.color(color);
    }

    public GuiLabel color(int color)
    {
        this.color = color;

        return this;
    }

    public GuiLabel background(int color)
    {
        this.background = color;

        return this;
    }

    public GuiLabel background(Supplier<Integer> color)
    {
        this.backgroundColor = color;

        return this;
    }

    public GuiLabel anchor(float x, float y)
    {
        this.anchorX = x;
        this.anchorY = y;

        return this;
    }

    public int getColor()
    {
        return this.backgroundColor == null ? this.background : this.backgroundColor.get();
    }

    @Override
    public boolean isEnabled()
    {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled)
    {}

    @Override
    public void draw(GuiContext context)
    {
        String label = this.label.get();
        int x = this.area.x(this.anchorX, this.font.getStringWidth(label));
        int y = this.area.y(this.anchorY, this.font.FONT_HEIGHT);

        GuiDraw.drawTextBackground(this.font, label, x, y, this.color, this.getColor());

        super.draw(context);
    }
}