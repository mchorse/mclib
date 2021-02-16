package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiCanvas;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.math.Variable;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiGraphPanel extends GuiDashboardPanel<GuiDashboard>
{
    public GuiGraphCanvas canvas;
    public GuiTextElement expression;
    public GuiIconElement help;

    public GuiGraphPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        this.canvas = new GuiGraphCanvas(mc);
        this.expression = new GuiTextElement(mc, 10000, this.canvas::parseExpression);
        this.help = new GuiIconElement(mc, Icons.HELP, (b) -> GuiUtils.openWebLink("https://github.com/mchorse/aperture/wiki/Math-Expressions"));
        this.help.tooltip(IKey.lang("mclib.gui.graph.help"), Direction.TOP);

        String first = "sin(x)";

        this.expression.setText(first);
        this.canvas.parseExpression(first);

        this.expression.flex().relative(this).x(10).y(1F, -30).w(1F, -20).h(20);
        this.canvas.flex().relative(this).wh(1F, 1F);
        this.help.flex().relative(this.expression).x(1F, -19).y(1).wh(18, 18);

        this.expression.add(this.help);
        this.add(this.canvas, this.expression);
    }

    @Override
    public boolean isClientSideOnly()
    {
        return true;
    }

    public static class GuiGraphCanvas extends GuiCanvas
    {
        private MathBuilder builder;
        private Variable x;
        private boolean first = true;

        public IValue expression;

        public GuiGraphCanvas(Minecraft mc)
        {
            super(mc);

            this.builder = new MathBuilder();
            this.builder.register(this.x = new Variable("x", 0));

            this.scaleY.inverse = true;
        }

        public void parseExpression(String expression)
        {
            try
            {
                this.expression = this.builder.parse(expression);
            }
            catch (Exception e)
            {
                this.expression = null;
            }
        }

        @Override
        public void resize()
        {
            super.resize();

            if (this.first)
            {
                this.scaleX.view(-10, 10);
                this.scaleX.calculateMultiplier();
                this.scaleY.view(-10, 10);
                this.scaleY.calculateMultiplier();

                this.first = false;
            }
        }

        @Override
        protected void drawCanvas(GuiContext context)
        {
            this.area.draw(0x88000000);

            this.drawVerticalGrid(context);
            this.drawHorizontalGridAndGraph(context);
        }

        private void drawVerticalGrid(GuiContext context)
        {
            /* Draw vertical grid */
            int ty = (int) this.scaleY.from(this.area.ey());
            int by = (int) this.scaleY.from(this.area.y - 12);

            int min = Math.min(ty, by) - 1;
            int max = Math.max(ty, by) + 1;
            int mult = this.scaleY.getMult();

            min -= min % mult + mult;
            max -= max % mult - mult;

            for (int j = 0, c = (max - min) / mult; j < c; j++)
            {
                int y = (int) this.scaleY.to(min + j * mult);

                if (y >= this.area.ey())
                {
                    continue;
                }

                Gui.drawRect(this.area.x, y, this.area.ex(), y + 1, 0x44ffffff);
                this.font.drawString(String.valueOf(min + j * mult), this.area.x + 4, y + 4, 0xffffff);
            }
        }

        private void drawHorizontalGridAndGraph(GuiContext context)
        {
            /* Draw scaling grid */
            int tx = (int) this.scaleX.from(this.area.ex());
            int bx = (int) this.scaleX.from(this.area.x);

            int min = Math.min(tx, bx) - 1;
            int max = Math.max(tx, bx) + 1;
            int mult = this.scaleX.getMult();

            min -= min % mult + mult;
            max -= max % mult - mult;

            for (int j = 0, c = (max - min) / mult; j < c; j++)
            {
                int x = (int) this.scaleX.to(min + j * mult);

                if (x >= this.area.ex())
                {
                    break;
                }

                Gui.drawRect(x, this.area.y, x + 1, this.area.ey(), 0x44ffffff);
                this.font.drawString(String.valueOf(min + j * mult), x + 4, this.area.y + 4, 0xffffff);
            }

            if (this.expression == null)
            {
                return;
            }

            if (Mouse.isButtonDown(0) && !context.isFocused())
            {
                int mouseX = context.mouseX;
                double x = this.scaleX.from(mouseX);

                this.x.set(x);
                double y = this.expression.get();
                boolean isNaN = Double.isNaN(y);

                String coordinate = "(" + GuiTrackpadElement.FORMAT.format(x) + ", " + (isNaN ? "undefined" : GuiTrackpadElement.FORMAT.format(y)) + ")";

                int y1 = context.mouseY;
                int y2 = (int) this.scaleY.to(y) + 1;

                if (y1 < y2)
                {
                    y1 -= 12;
                }

                if (!isNaN)
                {
                    Gui.drawRect(mouseX, Math.min(y1, y2), mouseX + 1, Math.max(y1, y2), 0xff57f52a);
                }

                int y3 = y1 < y2 ? y1 : y1 - 12;
                int w = this.font.getStringWidth(coordinate);

                mouseX += 1;

                Gui.drawRect(mouseX, y3, mouseX + w + 4, y3 + 12, 0xffffffff);
                this.font.drawString(coordinate, mouseX + 2, y3 + 2, 0x000000);
            }

            GlStateManager.glLineWidth(4);
            GlStateManager.disableTexture2D();
            BufferBuilder builder = Tessellator.getInstance().getBuffer();

            builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            int sub = 40;
            int gap = (max - min) * sub;

            for (int j = 1; j < gap; j++)
            {
                double previous = min + (j - 1) / (double) sub;
                double current = min + j / (double) sub;

                this.x.set(previous);
                double y1 = this.expression.get();

                this.x.set(current);
                double y2 = this.expression.get();

                double fx1 = this.scaleX.to(previous);
                double fy1 = this.scaleY.to(y1);
                double fx2 = this.scaleX.to(current);
                double fy2 = this.scaleY.to(y2);

                builder.pos(fx1, fy1, 0).color(0, 0.5F, 1F, 1F).endVertex();
                builder.pos(fx2, fy2, 0).color(0, 0.5F, 1F, 1F).endVertex();
            }

            Tessellator.getInstance().draw();
            GlStateManager.glLineWidth(1);
        }
    }
}
