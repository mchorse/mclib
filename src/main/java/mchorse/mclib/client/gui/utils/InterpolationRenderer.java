package mchorse.mclib.client.gui.utils;

import com.google.common.collect.ImmutableList;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.tooltips.styles.TooltipStyle;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.IInterpolation;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class InterpolationRenderer
{
    public static void drawInterpolationPreview(IInterpolation interp, GuiContext context, int x, int y, float anchorX, float anchorY, int duration)
    {
        if (interp == null)
        {
            return;
        }

        final float iterations = 40;
        final float padding = 50;

        int w = 140;
        int h = 130;

        TooltipStyle style = TooltipStyle.get();
        String tooltip = interp.getTooltip();
        List<String> lines = tooltip.isEmpty() ? ImmutableList.of() : context.font.listFormattedStringToWidth(tooltip, w - 20);
        int ah = lines.isEmpty() ? 0 : lines.size() * (context.font.FONT_HEIGHT + 4);

        y = MathUtils.clamp(y, 0, context.screen.height - h - ah);

        x -= (int) (w * anchorX);
        y -= (int) (h * anchorY);

        Area.SHARED.set(x, y, w, h + ah);
        style.drawBackground(Area.SHARED);

        Color fg = ColorUtils.COLOR.set(style.getForegroundColor(), false);
        int font = style.getTextColor();

        context.font.drawString(interp.getName(), x + 10, y + 10, font);

        for (int i = 0; i < lines.size(); i++)
        {
            context.font.drawString(lines.get(i), x + 10, y + h - 5 + i * (context.font.FONT_HEIGHT + 4), font);
        }

        BufferBuilder builder = Tessellator.getInstance().getBuffer();

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.glLineWidth(2F);

        builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        builder.pos(x + 10, y + 20, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + 10, y + h - 10, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + w / 2, y + 20, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + w / 2, y + h - 10, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + w - 10, y + 20, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + w - 10, y + h - 10, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();

        builder.pos(x + 10, y + 20, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + w - 10, y + 20, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + 10, y + 20 + (h - 30) / 2, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + w - 10, y + 20 + (h - 30) / 2, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + 10, y + h - 10, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();
        builder.pos(x + w - 10, y + h - 10, 0).color(fg.r, fg.g, fg.b, 0.2F).endVertex();

        builder.pos(x + 10, y + h - 10 - padding / 2, 0).color(fg.r, fg.g, fg.b, 0.11F).endVertex();
        builder.pos(x + w - 10, y + h - 10 - padding / 2, 0).color(fg.r, fg.g, fg.b, 0.11F).endVertex();
        builder.pos(x + 10, y + 20 + padding / 2, 0).color(fg.r, fg.g, fg.b, 0.11F).endVertex();
        builder.pos(x + w - 10, y + 20 + padding / 2, 0).color(fg.r, fg.g, fg.b, 0.11F).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.glLineWidth(3F);
        builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 1; i <= iterations; i++)
        {
            float factor0 = (i - 1) / iterations;
            float value0 = 1 - interp.interpolate(0, 1, factor0);
            float factor1 = i / iterations;
            float value1 = 1 - interp.interpolate(0, 1, factor1);

            float x1 = x + 10 + factor1 * (w - 20);
            float x2 = x + 10 + factor0 * (w - 20);
            float y1 = y + 20 + padding / 2 + value1 * (h - 30 - padding);
            float y2 = y + 20 + padding / 2 + value0 * (h - 30 - padding);

            builder.pos(x1, y1, 0).color(fg.r, fg.g, fg.b, 1F).endVertex();
            builder.pos(x2, y2, 0).color(fg.r, fg.g, fg.b, 1F).endVertex();
        }

        Tessellator.getInstance().draw();

        GlStateManager.enableTexture2D();

        context.font.drawString("A", x + 14, (int)(y + h - 10 - padding / 2) + 4, font);
        context.font.drawString("B", x + w - 19, (int)(y + 20 + padding / 2) - context.font.FONT_HEIGHT - 4, font);

        float tick = ((context.tick + context.partialTicks) % (duration + 20)) / (float) duration;
        float factor = MathUtils.clamp(tick, 0, 1);
        int px = x + w - 5;
        int py = y + 20 + (int) (padding / 2) + (int) ((1 - interp.interpolate(0, 1, factor)) * (h - 30 - padding));

        Gui.drawRect(px - 2, py - 2, px + 2, py + 2, 0xff000000 + fg.getRGBColor());
    }
}