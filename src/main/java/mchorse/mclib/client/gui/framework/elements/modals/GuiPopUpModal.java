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
    private boolean init = false;
    private int x0;
    private int y0;
    private Timer timer;

    private final int BACKGROUND_COLOR = ColorUtils.COLOR.set(0F, 0F, 0F, this.a).getRGBAColor();
    private final int TEXT_COLOR = 0xffffff;

    /** Alpha */
    private float a = 1F;

    /** Drop-shadow color */
    private int ds_c = McLib.primaryColor.get();

    /** Background color */
    private int bg_c = BACKGROUND_COLOR;

    /** Text color */
    private int tx_c = TEXT_COLOR;

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
            this.x0 = context.mouseX()-this.area.w/4;
            this.y0 = context.mouseY()-this.area.h/2;

            this.init = true;
        }

        this.area.x = this.x0;
        this.area.y = this.y0;

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), bg_c);
        GuiDraw.drawDropShadow(area.x, area.y, area.ex(), area.ey(), 6, 0x44000000 + ds_c, ds_c);

        this.y = 0;
        int y = this.area.y + 10; //font offset

        for (String line : this.font.listFormattedStringToWidth(this.label.get(), this.area.w - 20))
        {
            this.font.drawStringWithShadow(line, this.area.x + 10, y + this.y, tx_c);
            this.y += 11;
        }

        if (!this.area.isInside(context))
        {
            if (timer == null)
            {
                timer = new Timer(duration);
                timer.mark();
            }

            float x = ((float)duration - timer.getRemaining())/duration;

            this.bg_c = fadeAlpha(this.bg_c, x);
            this.tx_c = fadeAlpha(this.tx_c, x);

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
            this.bg_c = BACKGROUND_COLOR;
            this.tx_c = TEXT_COLOR;
            this.ds_c = McLib.primaryColor.get();
        }

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    private int fadeAlpha(int color, float x)
    {
        ColorUtils.COLOR.set(color, true).a = Interpolations.lerp(this.a, 0F, x);

        return ColorUtils.COLOR.getRGBAColor();
    }
}
