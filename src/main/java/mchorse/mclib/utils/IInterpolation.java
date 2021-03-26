package mchorse.mclib.utils;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IInterpolation
{
    public float interpolate(float a, float b, float x);

    public double interpolate(double a, double b, double x);

    @SideOnly(Side.CLIENT)
    public default String getName()
    {
        return I18n.format(this.getKey());
    }

    @SideOnly(Side.CLIENT)
    public String getKey();

    @SideOnly(Side.CLIENT)
    public default String getTooltip()
    {
        return I18n.format(this.getTooltipKey());
    }

    @SideOnly(Side.CLIENT)
    public String getTooltipKey();
}
