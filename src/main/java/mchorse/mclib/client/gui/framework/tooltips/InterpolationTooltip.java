package mchorse.mclib.client.gui.framework.tooltips;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.InterpolationRenderer;
import mchorse.mclib.utils.IInterpolation;

import java.util.function.Supplier;

public class InterpolationTooltip implements ITooltip
{
    public float ax;
    public float ay;
    public Supplier<IInterpolation> interpolation;
    public Supplier<Integer> duration;
    public int margin = 10;

    public InterpolationTooltip(float ax, float ay, Supplier<IInterpolation> interpolation, Supplier<Integer> duration)
    {
        this.ax = ax;
        this.ay = ay;
        this.interpolation = interpolation;
        this.duration = duration;
    }

    public InterpolationTooltip margin(int margin)
    {
        this.margin = margin;

        return this;
    }

    @Override
    public void drawTooltip(GuiContext context)
    {
        Area area = context.tooltip.area;
        IInterpolation interpolation = this.interpolation == null ? null : this.interpolation.get();
        int duration = this.duration == null ? 40 : this.duration.get();

        float fx = (this.ax - 0.5F) * 2;

        int x = area.x(this.ax) + (int) (this.margin * fx);
        int y = area.y(this.ay);

        InterpolationRenderer.drawInterpolationPreview(interpolation, context, x, y, 1 - this.ax, this.ay, duration);
    }
}