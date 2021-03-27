package mchorse.mclib.utils.keyframes;

import mchorse.mclib.utils.IInterpolation;
import mchorse.mclib.utils.Interpolations;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class KeyframeInterpolations
{
    public static final IInterpolation CONSTANT = new IInterpolation()
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return a;
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return a;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getKey()
        {
            return "mclib.interpolations.const";
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getTooltipKey()
        {
            return "mclib.interpolations.tooltips.const";
        }
    };

    public static final IInterpolation HERMITE = new IInterpolation()
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return (float) Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getKey()
        {
            return "mclib.interpolations.hermite";
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getTooltipKey()
        {
            return "mclib.interpolations.tooltips.hermite";
        }
    };

    public static final IInterpolation BEZIER = new IInterpolation()
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return (float) Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getKey()
        {
            return "mclib.interpolations.bezier";
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getTooltipKey()
        {
            return "mclib.interpolations.tooltips.bezier";
        }
    };
}