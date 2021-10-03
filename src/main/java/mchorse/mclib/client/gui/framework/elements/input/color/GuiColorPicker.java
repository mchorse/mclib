package mchorse.mclib.client.gui.framework.elements.input.color;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Color picker element
 *
 * This is the one that is responsible for picking colors
 */
public class GuiColorPicker extends GuiElement
{
    public static final int COLOR_SLIDER_HEIGHT = 50;
    public static final IKey FAVORITE = IKey.lang("mclib.gui.color.favorite");
    public static final IKey RECENT = IKey.lang("mclib.gui.color.recent");

    public static List<Color> recentColors = new ArrayList<Color>();

    public Color color = new Color();
    public Consumer<Integer> callback;

    public GuiTextElement input;
    public GuiColorPalette recent;
    public GuiColorPalette favorite;

    public boolean editAlpha;

    public Area red = new Area();
    public Area green = new Area();
    public Area blue = new Area();
    public Area alpha = new Area();
    public Area preview = new Area();

    public int dragging = -1;

    public static void drawAlphaPreviewQuad(int x1, int y1, int x2, int y2, Color color)
    {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(x1, y1, 0).color(color.r, color.g, color.b, 1).endVertex();
        vertexbuffer.pos(x1, y2, 0).color(color.r, color.g, color.b, 1).endVertex();
        vertexbuffer.pos(x2, y1, 0).color(color.r, color.g, color.b, 1).endVertex();
        vertexbuffer.pos(x2, y1, 0).color(color.r, color.g, color.b, color.a).endVertex();
        vertexbuffer.pos(x1, y2, 0).color(color.r, color.g, color.b, color.a).endVertex();
        vertexbuffer.pos(x2, y2, 0).color(color.r, color.g, color.b, color.a).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.enableTexture2D();
    }

    public GuiColorPicker(Minecraft mc, Consumer<Integer> callback)
    {
        super(mc);

        this.callback = callback;

        this.input = new GuiTextElement(mc, 7, (string) ->
        {
            this.setValue(ColorUtils.parseColor(string));
            this.callback();
        });

        this.recent = new GuiColorPalette(mc, (color) ->
        {
            this.setColor(color.getRGBAColor());
            this.updateColor();
        }).colors(recentColors);

        this.recent.context(() ->
        {
            GuiContext context = GuiBase.getCurrent();
            int index = this.recent.getIndex(context);

            if (!this.recent.hasColor(index))
            {
                return null;
            }

            return new GuiSimpleContextMenu(Minecraft.getMinecraft())
                .action(Icons.FAVORITE, IKey.lang("mclib.gui.color.context.favorites.add"), () -> this.addToFavorites(this.recent.colors.get(index)));
        });

        this.favorite = new GuiColorPalette(mc, (color) ->
        {
            this.setColor(color.getRGBAColor());
            this.updateColor();
        }).colors(McLib.favoriteColors.getCurrentColors());

        this.favorite.context(() ->
        {
            GuiContext context = GuiBase.getCurrent();
            int index = this.favorite.getIndex(context);

            if (!this.favorite.hasColor(index))
            {
                return null;
            }

            return new GuiSimpleContextMenu(Minecraft.getMinecraft())
                .action(Icons.REMOVE, IKey.lang("mclib.gui.color.context.favorites.remove"), () -> this.removeFromFavorites(index));
        });

        this.input.flex().relative(this).set(5, 5, 0, 20).w(1, -35);
        this.favorite.flex().relative(this).xy(5, 95).w(1F, -10);
        this.recent.flex().relative(this.favorite).w(1F);

        this.hideTooltip().add(this.input, this.favorite, this.recent);
    }

    public GuiColorPicker editAlpha()
    {
        this.editAlpha = true;
        this.input.field.setMaxStringLength(9);

        return this;
    }

    public void updateField()
    {
        this.input.setText(this.color.stringify(this.editAlpha));
    }

    public void updateColor()
    {
        this.updateField();
        this.callback();
    }

    protected void callback()
    {
        if (this.callback != null)
        {
            this.callback.accept(this.editAlpha ? this.color.getRGBAColor() : this.color.getRGBColor());
        }
    }

    public void setColor(float r, float g, float b, float a)
    {
        this.color.set(r, g, b, a);
        this.updateField();
    }

    public void setColor(int color)
    {
        this.setValue(color);
        this.updateField();
    }

    public void setValue(int color)
    {
        this.color.set(color, this.editAlpha);
    }

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        if (!this.preview.isInside(context))
        {
            return super.createContextMenu(context);
        }

        return new GuiSimpleContextMenu(this.mc)
            .action(Icons.FAVORITE, IKey.lang("mclib.gui.color.context.favorites.add"), () -> this.addToFavorites(this.color));
    }

    public void setup(int x, int y)
    {
        this.flex().xy(x, y);
        this.setupSize();
    }

    private void setupSize()
    {
        int width = 200;
        int recent = this.recent.colors.isEmpty() ? 0 : this.recent.getHeight(width - 10);
        int favorite = this.favorite.colors.isEmpty() ? 0 : this.favorite.getHeight(width - 10);
        int base = 85;

        base += favorite > 0 ? favorite + 15 : 0;
        base += recent > 0 ? recent + 15 : 0;

        this.flex().h(base);
        this.favorite.flex().h(favorite);
        this.recent.flex().h(recent);

        if (favorite > 0)
        {
            this.recent.flex().y(1F, 15);
        }
        else
        {
            this.recent.flex().y(0);
        }
    }

    /* Managing recent and favorite colors */

    private void addToRecent()
    {
        this.addColor(recentColors, this.color);
    }

    private void addToFavorites(Color color)
    {
        this.addColor(McLib.favoriteColors.getCurrentColors(), color);
        McLib.favoriteColors.saveLater();

        this.setupSize();
        this.resize();
    }

    private void removeFromFavorites(int index)
    {
        McLib.favoriteColors.getCurrentColors().remove(index);
        McLib.favoriteColors.saveLater();

        this.setupSize();
        this.resize();
    }

    private void addColor(List<Color> colors, Color color)
    {
        int i = colors.indexOf(color);

        if (i == -1)
        {
            colors.add(color.copy());
        }
        else
        {
            colors.add(colors.remove(i));
        }
    }

    /* GuiElement overrides */

    @Override
    public void resize()
    {
        super.resize();

        int c = this.editAlpha ? 4 : 3;
        int h = COLOR_SLIDER_HEIGHT / c;
        int w = this.area.w - 10;
        int remainder = COLOR_SLIDER_HEIGHT - h * c;
        int y = this.area.y + 30;

        this.preview.setPoints(this.area.ex() - 25, this.area.y + 5, this.area.ex() - 5, this.area.y + 25);
        this.red.set(this.area.x + 5, y, w, h);

        if (this.editAlpha)
        {
            this.green.set(this.area.x + 5, y + h, w, h);
            this.blue.set(this.area.x + 5, y + h + h, w, h + remainder);
            this.alpha.set(this.area.x + 5, y + COLOR_SLIDER_HEIGHT - h, w, h);
        }
        else
        {
            this.green.set(this.area.x + 5, y + h, w, h + remainder);
            this.blue.set(this.area.x + 5, y + COLOR_SLIDER_HEIGHT - h, w, h);
        }
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.red.isInside(context))
        {
            this.dragging = 1;

            return true;
        }
        else if (this.green.isInside(context))
        {
            this.dragging = 2;

            return true;
        }
        else if (this.blue.isInside(context))
        {
            this.dragging = 3;

            return true;
        }
        else if (this.alpha.isInside(context) && this.editAlpha)
        {
            this.dragging = 4;

            return true;
        }

        if (!this.area.isInside(context))
        {
            this.removeFromParent();
            this.addToRecent();

            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);
        this.dragging = -1;
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.dragging >= 0)
        {
            float factor = (context.mouseX - (this.red.x + 7)) / (float) (this.red.w - 14);

            this.color.set(MathUtils.clamp(factor, 0, 1), this.dragging);
            this.updateColor();
        }

        int padding = GuiDraw.drawBorder(this.area, 0xffffffff);

        this.area.draw(0xffc6c6c6, padding + 1);
        this.drawRect(this.area.ex() - 25, this.area.y + 5, this.area.ex() - 5, this.area.y + 25);

        GuiDraw.drawOutline(this.area.ex() - 25, this.area.y + 5, this.area.ex() - 5, this.area.y + 25, 0x44000000);

        if (this.editAlpha)
        {
            Icons.CHECKBOARD.renderArea(this.alpha.x, this.red.y, this.alpha.w, this.alpha.ey() - this.red.y);
        }

        Color color = new Color();

        /* Draw red slider */
        color.copy(this.color).r = 0;
        int left = color.getRGBAColor();
        color.copy(this.color).r = 1;
        int right = color.getRGBAColor();

        GuiDraw.drawHorizontalGradientRect(this.red.x, this.red.y, this.red.ex(), this.red.ey(), left, right);

        /* Draw green slider */
        color.copy(this.color).g = 0;
        left = color.getRGBAColor();
        color.copy(this.color).g = 1;
        right = color.getRGBAColor();

        GuiDraw.drawHorizontalGradientRect(this.green.x, this.green.y, this.green.ex(), this.green.ey(), left, right);

        /* Draw blue slider */
        color.copy(this.color).b = 0;
        left = color.getRGBAColor();
        color.copy(this.color).b = 1;
        right = color.getRGBAColor();

        GuiDraw.drawHorizontalGradientRect(this.blue.x, this.blue.y, this.blue.ex(), this.blue.ey(), left, right);

        if (this.editAlpha)
        {
            /* Draw alpha slider */
            color.copy(this.color).a = 0;
            left = color.getRGBAColor();
            color.copy(this.color).a = 1;
            right = color.getRGBAColor();

            GuiDraw.drawHorizontalGradientRect(this.alpha.x, this.alpha.y, this.alpha.ex(), this.alpha.ey(), left, right);
        }

        GuiDraw.drawOutline(this.red.x, this.red.y, this.red.ex(), this.editAlpha ? this.alpha.ey() : this.blue.ey(), 0x44000000);

        this.drawMarker(this.red.x + 7 + (int) ((this.red.w - 14) * this.color.r), this.red.my());
        this.drawMarker(this.green.x + 7 + (int) ((this.green.w - 14) * this.color.g), this.green.my());
        this.drawMarker(this.blue.x + 7 + (int) ((this.blue.w - 14) * this.color.b), this.blue.my());

        if (this.editAlpha)
        {
            this.drawMarker(this.alpha.x + 7 + (int) ((this.alpha.w - 14) * this.color.a), this.alpha.my());
        }

        if (!this.favorite.colors.isEmpty())
        {
            this.font.drawString(FAVORITE.get(), this.favorite.area.x, this.favorite.area.y - 10, 0x888888);
        }

        if (!this.recent.colors.isEmpty())
        {
            this.font.drawString(RECENT.get(), this.recent.area.x, this.recent.area.y - 10, 0x888888);
        }

        super.draw(context);
    }

    public void drawRect(int x1, int y1, int x2, int y2)
    {
        if (this.editAlpha)
        {
            Icons.CHECKBOARD.renderArea(x1, y1,x2 - x1, y2 - y1);
            drawAlphaPreviewQuad(x1, y1, x2, y2, this.color);
        }
        else
        {
            Gui.drawRect(x1, y1, x2, y2, this.color.getRGBAColor());
        }
    }

    private void drawMarker(int x, int y)
    {
        Gui.drawRect(x - 4, y - 4, x + 4, y + 4, 0xff000000);
        Gui.drawRect(x - 3, y - 3, x + 3, y + 3, 0xffffffff);
        Gui.drawRect(x - 2, y - 2, x + 2, y + 2, 0xffc6c6c6);
    }
}