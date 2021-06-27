package mchorse.mclib.client.gui.framework.elements.context;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiSimpleContextMenu extends GuiContextMenu
{
    public GuiListElement<Action> actions;

    private boolean shadow;

    public GuiSimpleContextMenu(Minecraft mc)
    {
        super(mc);

        this.actions = new GuiActionListElement(mc, (action) ->
        {
            if (action.get(0).runnable != null)
            {
                action.get(0).runnable.run();
            }

            this.removeFromParent();
        });

        this.actions.flex().relative(this).w(1, 0).h(1, 0);
        this.add(this.actions);
    }

    public GuiSimpleContextMenu shadow()
    {
        this.shadow = true;

        return this;
    }

    public GuiSimpleContextMenu action(IKey label, Runnable runnable)
    {
        return this.action(Icons.NONE, label, runnable);
    }

    public GuiSimpleContextMenu action(Icon icon, IKey label, Runnable runnable)
    {
        if (icon == null || label == null)
        {
            return this;
        }

        return this.action(new Action(icon, label, runnable));
    }

    public GuiSimpleContextMenu action(Icon icon, IKey label, Runnable runnable, int color)
    {
        if (icon == null || label == null)
        {
            return this;
        }

        return this.action(new ColorfulAction(icon, label, runnable, color));
    }

    public GuiSimpleContextMenu action(Action action)
    {
        this.actions.add(action);

        return this;
    }

    @Override
    public void setMouse(GuiContext context)
    {
        int w = 100;

        for (Action action : this.actions.getList())
        {
            w = Math.max(action.getWidth(this.font), w);
        }

        Supplier<Float> h = () ->
        {
            return (float) Math.min(this.actions.scroll.scrollSize, context.screen.height - 10);
        };

        this.flex().set(context.mouseX(), context.mouseY(), w, 0).h(h).bounds(context.screen.root, 5);
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.shadow)
        {
            int color = McLib.primaryColor.get();

            GuiDraw.drawDropShadow(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 10, 0x44000000 + color, color);
        }

        super.draw(context);
    }

    public static class GuiActionListElement extends GuiListElement<Action>
    {
        public GuiActionListElement(Minecraft mc, Consumer<List<Action>> callback)
        {
            super(mc, callback);

            this.scroll.scrollItemSize = 20;
        }

        @Override
        public void drawListElement(Action element, int i, int x, int y, boolean hover, boolean selected)
        {
            int h = this.scroll.scrollItemSize;

            element.draw(this.font, x, y, this.scroll.w, h, hover, selected);
        }
    }

    public static class Action
    {
        public Icon icon;
        public IKey label;
        public Runnable runnable;

        public Action(Icon icon, IKey label, Runnable runnable)
        {
            this.icon = icon;
            this.label = label;
            this.runnable = runnable;
        }

        public int getWidth(FontRenderer font)
        {
            return 28 + font.getStringWidth(this.label.get());
        }

        public void draw(FontRenderer font, int x, int y, int w, int h, boolean hover, boolean selected)
        {
            this.drawBackground(font, x, y, w, h, hover, selected);

            GlStateManager.color(1, 1, 1, 1);
            this.icon.render(x + 2, y + h / 2, 0, 0.5F);
            font.drawString(this.label.get(), x + 22, y + (h - font.FONT_HEIGHT) / 2 + 1, 0xffffff);
        }

        protected void drawBackground(FontRenderer font, int x, int y, int w, int h, boolean hover, boolean selected)
        {
            if (hover)
            {
                Gui.drawRect(x, y, x + w, y + h, ColorUtils.HALF_BLACK + McLib.primaryColor.get());
            }
        }
    }

    public static class ColorfulAction extends Action
    {
        public int color;

        public ColorfulAction(Icon icon, IKey label, Runnable runnable, int color)
        {
            super(icon, label, runnable);

            this.color = color;
        }

        @Override
        protected void drawBackground(FontRenderer font, int x, int y, int w, int h, boolean hover, boolean selected)
        {
            super.drawBackground(font, x, y, w, h, hover, selected);

            drawRect(x, y, x + 2, y + h, 0xff000000 + this.color);
            GuiDraw.drawHorizontalGradientRect(x + 2, y, x + 24, y + h, 0x44000000 + this.color, this.color);
        }
    }
}