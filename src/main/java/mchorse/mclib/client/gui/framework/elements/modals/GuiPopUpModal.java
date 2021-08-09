package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GuiPopUpModal extends GuiModal
{
    private static final Color BACKGROUND_COLOR = new Color(0F, 0F, 0F, 1F);
    private static final Color TEXT_COLOR = new Color(1F, 1F, 1F, 1F);

    private boolean init = false;
    private int x0;
    private int y0;
    private Timer timer;

    /* Colours */
    private float alpha = 1F;
    private float shadowAlpha = 0.26666668F;
    private Color shadowColor = (new Color(McLib.primaryColor.get())).set(this.shadowAlpha, 4);
    private Color backgroundColor = BACKGROUND_COLOR.copy();
    private Color textColor = TEXT_COLOR.copy();

    /**
     * fade duration in milliseconds
     */
    private int duration = 500;

    public GuiPopUpModal(Minecraft mc, IKey label)
    {
        super(mc, label);
    }

    /**
     * Set the duration for the fading animation if it hadn't already begun
     *
     * @param duration in milliseconds
     */
    public void setFadeDuration(int duration)
    {
        if (this.timer == null)
        {
            this.duration = duration;
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        if (!this.init)
        {
            this.x0 = context.mouseX() - this.area.w / 4;
            this.y0 = context.mouseY() - this.area.h / 2;

            this.init = true;
        }

        this.area.x = this.x0;
        this.area.y = this.y0;

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        GuiDraw.drawDropShadow(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 6, this.shadowColor.getRGBAColor(), this.shadowColor.getRGBAColor());
        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), this.backgroundColor.getRGBAColor());
        System.out.println(shadowColor.a);
        this.y = 0;
        int y = this.area.y + 10; //font offset

        for (String line : this.font.listFormattedStringToWidth(this.label.get(), this.area.w - 20))
        {
            this.font.drawStringWithShadow(line, this.area.x + 10, y + this.y, this.textColor.getRGBAColor());
            this.y += 11;
        }

        if (!this.area.isInside(context))
        {
            if (this.timer == null)
            {
                this.timer = new Timer(this.duration);
                this.timer.mark();
            }

            float x = ((float)this.duration - this.timer.getRemaining()) / this.duration;

            fadeAlpha(this.backgroundColor, this.alpha, x);
            fadeAlpha(this.textColor, this.alpha, x);
            fadeAlpha(this.shadowColor, this.shadowAlpha, x);

            if(this.timer.check())
            {
                context.postRenderCallbacks.add((c) ->
                {
                    this.removeFromParent();
                });
            }
        }
        else if (this.timer != null && !this.timer.check()) //if the mouse came back before the timer ended
        {
            this.timer = null;
            this.backgroundColor = BACKGROUND_COLOR.copy();
            this.textColor = TEXT_COLOR.copy();
            this.shadowColor = new Color(McLib.primaryColor.get()).set(this.shadowAlpha, 4);
        }

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    private void fadeAlpha(Color color, float alpha, float x)
    {
        color.a = Interpolations.lerp(alpha, 0F, x);
    }
}
