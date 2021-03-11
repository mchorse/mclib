package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.color.GuiColorPicker;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

/**
 * Color GUI element
 *
 * This class is responsible for providing a way to edit colors, this element
 * itself is not editing the color, the picker element is the one that does color editing
 */
public class GuiColorElement extends GuiElement
{
    public GuiColorPicker picker;
    public boolean label = true;
    public Direction direction;
    public GuiElement target;

    public GuiColorElement(Minecraft mc, ValueInt value)
    {
        this(mc, value, null);
    }

    public GuiColorElement(Minecraft mc, ValueInt value, Consumer<Integer> callback)
    {
        this(mc, callback == null ? value::set : (integer) ->
        {
            value.set(integer);
            callback.accept(integer);
        });
        this.tooltip(IKey.lang(value.getCommentKey()));

        if (value.getSubtype() == ValueInt.Subtype.COLOR_ALPHA)
        {
            this.picker.editAlpha();
        }

        this.picker.setColor(value.get());
    }

    public GuiColorElement(Minecraft mc, Consumer<Integer> callback)
    {
        super(mc);

        this.picker = new GuiColorPicker(mc, (color) ->
        {
            if (callback != null)
            {
                callback.accept(color);
            }
        });
        this.picker.markIgnored().flex().wh(200, 85).bounds(this, 2);

        this.direction(Direction.BOTTOM).flex().h(20);
    }

    public GuiColorElement direction(Direction direction)
    {
        this.direction = direction;
        this.picker.flex().anchor(1 - direction.anchorX, 1 - direction.anchorY);

        return this;
    }

    public GuiColorElement onTop()
    {
        return this.direction(Direction.TOP);
    }

    public GuiColorElement noLabel()
    {
        this.label = false;

        return this;
    }

    public GuiColorElement target(GuiElement target)
    {
        this.target = target;
        this.picker.flex().bounds(null, 0).target = target;

        return this;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.area.isInside(context))
        {
            if (!this.picker.hasParent())
            {
                int x = context.globalX(this.area.x(this.direction.anchorX) + 2 * this.direction.factorX);
                int y = context.globalY(this.area.y(this.direction.anchorY) + 2 * this.direction.factorY);

                (this.target == null ? this.getParentContainer() : this.target).add(this.picker);
                this.picker.setup(x, y);
                this.picker.resize();
            }
            else
            {
                this.picker.removeFromParent();
            }

            return true;
        }

        return false;
    }

    @Override
    public void draw(GuiContext context)
    {
        int padding = 0;

        if (McLib.enableBorders.get())
        {
            this.area.draw(0xff000000);

            GlStateManager.color(1, 1, 1);
            this.picker.drawRect(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1);

            padding = 1;
        }
        else
        {
            this.picker.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey());
        }

        if (this.area.isInside(context))
        {
            this.area.draw(0x22000000, padding);
        }

        if (this.label)
        {
            String label = this.picker.color.stringify(this.picker.editAlpha);

            GuiDraw.drawTextBackground(this.font, label, this.area.mx(this.font.getStringWidth(label)), this.area.my(this.font.FONT_HEIGHT - 1), 0xffffff, 0x55000000, 1);
        }

        GuiDraw.drawLockedArea(this);

        super.draw(context);
    }
}