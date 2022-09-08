package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class GuiLabel extends GuiElement implements ITextColoring
{
    public IKey label;
    public int color;
    public boolean textShadow = true;
    public float anchorX;
    public float anchorY;
    public int background;
    public Supplier<Integer> backgroundColor;
    private IconContainer leftIcon;
    private IconContainer rightIcon;

    public GuiLabel(Minecraft mc, IKey label)
    {
        this(mc, label, 0xffffff);
    }

    public GuiLabel(Minecraft mc, IKey label, int color)
    {
        this(mc, label, 0xffffff, (IconContainer) null, null);
    }

    public GuiLabel(Minecraft mc, IKey label, int color, @Nullable Icon leftIcon, @Nullable Icon rightIcon)
    {
        super(mc);

        this.label = label;
        this.color = color;
        this.leftIcon = leftIcon != null ? new IconContainer(leftIcon) : null;
        this.rightIcon = rightIcon != null ? new IconContainer(rightIcon) : null;
    }

    public GuiLabel(Minecraft mc, IKey label, int color, @Nullable IconContainer leftIcon, @Nullable IconContainer rightIcon)
    {
        super(mc);

        this.label = label;
        this.color = color;
        this.leftIcon = leftIcon;
        this.rightIcon = rightIcon;
    }

    public GuiLabel(Minecraft mc, IKey label, @Nullable Icon leftIcon, @Nullable Icon rightIcon)
    {
        this(mc, label, 0xffffff, leftIcon, rightIcon);
    }

    public IconContainer getLeftIcon()
    {
        return this.leftIcon;
    }

    public void setLeftIcon(Icon leftIcon)
    {
        if (this.leftIcon != null)
        {
            this.leftIcon.setIcon(leftIcon);
        }
        else
        {
            this.leftIcon = new IconContainer(leftIcon);
        }
    }

    public IconContainer getRightIcon()
    {
        return this.rightIcon;
    }

    public void setRightIcon(Icon rightIcon)
    {
        if (this.rightIcon != null)
        {
            this.rightIcon.setIcon(rightIcon);
        }
        else
        {
            this.rightIcon = new IconContainer(rightIcon);
        }
    }

    public void setRightIconRenderer(IconContainer rightIcon)
    {
        this.rightIcon = rightIcon;
    }

    public void setLeftIconRenderer(IconContainer leftIcon)
    {
        this.leftIcon = leftIcon;
    }

    @Override
    public void setColor(int color, boolean shadow)
    {
        this.color(color, shadow);
    }

    public GuiLabel color(int color)
    {
        return this.color(color, true);
    }

    public GuiLabel color(int color, boolean textShadow)
    {
        this.textShadow = textShadow;
        this.color = color;

        return this;
    }

    public GuiLabel background()
    {
        return this.background(ColorUtils.HALF_BLACK);
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
    public void draw(GuiContext context)
    {
        int offset = 3;
        int iconLeftOffset = this.leftIcon != null ? this.leftIcon.getW() + offset : 0;
        int iconRightOffset = this.rightIcon != null ? this.rightIcon.getH() + offset : 0;
        int width = this.font.getStringWidth(this.label.get()) + 2 * offset + iconRightOffset + iconLeftOffset;
        int x0 = this.area.x(this.anchorX, width);
        int x1 = x0 + width;
        int y = this.area.y(this.anchorY, this.font.FONT_HEIGHT);

        int xText = x0 + offset + iconLeftOffset;

        int a = this.getColor() >> 24 & 0xff;

        if (a != 0)
        {
            Gui.drawRect(x0, y - offset, x1, y + font.FONT_HEIGHT, this.getColor());
        }

        GlStateManager.color(1,1,1,1);

        if (this.leftIcon != null)
        {
            this.leftIcon.render(x0 + offset, y, 0.5F, 0.5F);
        }

        if (this.rightIcon != null)
        {
            this.rightIcon.render(xText + offset + this.font.getStringWidth(this.label.get()), y, 0.5F, 0.5F);
        }

        this.font.drawString(this.label.get(), xText, y, this.color, this.textShadow);

        super.draw(context);
    }
}