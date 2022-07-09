package mchorse.mclib.client.gui.framework.elements.modals;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MathUtils;
import mchorse.mclib.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GuiPopUpModal extends GuiModal
{
    private Color backgroundColorDefault = new Color(0F, 0F, 0F, 0F);
    private Color textColorDefault = new Color(1F, 1F, 1F, 0F);
    private float shadowAlphaDefault = 0.26666668F;
    private float alphaDefault = 1F;

    private boolean init = false;
    private int x0;
    private int y0;
    private Timer timer;

    /* Colours */
    private float shadowAlpha;
    private float textAlpha;
    private float backgroundAlpha;

    private Color shadowColor;
    private Color backgroundColor;
    private Color textColor;

    /**
     * fade duration in milliseconds
     */
    private int duration = 350;

    public GuiPopUpModal(Minecraft mc, IKey label)
    {
        super(mc, label);

        this.defaultColors();
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

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);

        int shadowAlpha = (int)(this.shadowAlpha * 255F) << 24;
        int textAlpha = (int)(this.textAlpha * 255F) << 24;
        int backgroundAlpha = (int)(this.backgroundAlpha * 255F) << 24;

        GuiDraw.drawDropShadow(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 6, shadowAlpha + this.shadowColor.getRGBColor(), this.shadowColor.getRGBColor());
        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), backgroundAlpha + this.backgroundColor.getRGBAColor());

        this.y = 0;

        for (String line : this.font.listFormattedStringToWidth(this.label.get(), this.area.w - 20))
        {
            this.font.drawStringWithShadow(line, this.area.x + 10, this.area.y + 10 + this.y, textAlpha + this.textColor.getRGBAColor());

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

            this.backgroundAlpha = this.fadeAlpha(this.alphaDefault, 0F, x);
            this.textAlpha = this.fadeAlpha(this.alphaDefault, 0F, x);
            this.shadowAlpha =this.fadeAlpha(this.shadowAlphaDefault, 0F,  MathUtils.clamp(x * 1.7F, 0, 1));

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

            this.defaultColors();
        }

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    public void defaultColors()
    {
        this.shadowAlpha = this.shadowAlphaDefault;
        this.textAlpha = this.alphaDefault;
        this.backgroundAlpha = this.alphaDefault;

        this.shadowColor = new Color(McLib.primaryColor.get());
        this.backgroundColor = this.backgroundColorDefault.copy();
        this.textColor = this.textColorDefault.copy();
    }

    private float fadeAlpha(float a, float b, float x)
    {
        return Interpolations.lerp(a, b, x);
    }
}
