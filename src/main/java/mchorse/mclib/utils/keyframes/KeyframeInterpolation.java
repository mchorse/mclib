package mchorse.mclib.utils.keyframes;

import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MathUtils;

public enum KeyframeInterpolation
{
    CONST("const")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            return a.value;
        }
    },
    LINEAR("linear")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            return Interpolations.lerp(a.value, b.value, x);
        }
    },
    QUAD("quad")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            if (a.easing == KeyframeEasing.IN) return Interpolation.QUAD_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT) return Interpolation.QUAD_OUT.interpolate(a.value, b.value, x);

            return Interpolation.QUAD_INOUT.interpolate(a.value, b.value, x);
        }
    },
    CUBIC("cubic")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            if (a.easing == KeyframeEasing.IN) return Interpolation.CUBIC_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT) return Interpolation.CUBIC_OUT.interpolate(a.value, b.value, x);

            return Interpolation.CUBIC_INOUT.interpolate(a.value, b.value, x);
        }
    },
    HERMITE("hermite")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            double v0 = a.prev.value;
            double v1 = a.value;
            double v2 = b.value;
            double v3 = b.next.value;

            return Interpolations.cubicHermite(v0, v1, v2, v3, x);
        }
    },
    EXP("exp")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            if (a.easing == KeyframeEasing.IN) return Interpolation.EXP_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT) return Interpolation.EXP_OUT.interpolate(a.value, b.value, x);

            return Interpolation.EXP_INOUT.interpolate(a.value, b.value, x);
        }
    },
    BEZIER("bezier")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            if (x <= 0) return a.value;
            if (x >= 1) return b.value;

            /* Transform input to 0..1 */
            double w = b.tick - a.tick;
            double h = b.value - a.value;

            /* In case if there is no slope whatsoever */
            if (h == 0) h = 0.00001;

            double x1 = a.rx / w;
            double y1 = a.ry / h;
            double x2 = (w - b.lx) / w;
            double y2 = (h + b.ly) / h;
            double e = 0.0005;

            e = h == 0 ? e : Math.max(Math.min(e, 1 / h * e), 0.00001);
            x1 = MathUtils.clamp(x1, 0, 1);
            x2 = MathUtils.clamp(x2, 0, 1);

            return Interpolations.bezier(0, y1, y2, 1, Interpolations.bezierX(x1, x2, x, e)) * h + a.value;
        }
    },
    BACK("back")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            if (a.easing == KeyframeEasing.IN) return Interpolation.BACK_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT) return Interpolation.BACK_OUT.interpolate(a.value, b.value, x);

            return Interpolation.BACK_INOUT.interpolate(a.value, b.value, x);
        }
    },
    ELASTIC("elastic")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            if (a.easing == KeyframeEasing.IN) return Interpolation.ELASTIC_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT) return Interpolation.ELASTIC_OUT.interpolate(a.value, b.value, x);

            return Interpolation.ELASTIC_INOUT.interpolate(a.value, b.value, x);
        }
    },
    BOUNCE("bounce")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            if (a.easing == KeyframeEasing.IN) return Interpolation.BOUNCE_IN.interpolate(a.value, b.value, x);
            if (a.easing == KeyframeEasing.OUT) return Interpolation.BOUNCE_OUT.interpolate(a.value, b.value, x);

            return Interpolation.BOUNCE_INOUT.interpolate(a.value, b.value, x);
        }
    };

    public final String key;

    private KeyframeInterpolation(String key)
    {
        this.key = key;
    }

    public abstract double interpolate(Keyframe a, Keyframe b, float x);

    public String getKey()
    {
        return "mclib.interpolations." + this.key;
    }
}
