package mchorse.mclib.utils.keyframes;

import mchorse.mclib.utils.Interpolations;
import net.minecraft.util.math.MathHelper;

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
            if (a.easing == KeyframeEasing.IN) return a.value + (b.value - a.value) * x * x;
            if (a.easing == KeyframeEasing.OUT) return a.value - (b.value - a.value) * x * (x - 2);

            x *= 2;

            if (x < 1D) return a.value + (b.value - a.value) / 2 * x * x;

            x -= 1;

            return a.value - (b.value - a.value) / 2 * (x * (x - 2) - 1);
        }
    },
    CUBIC("cubic")
    {
        @Override
        public double interpolate(Keyframe a, Keyframe b, float x)
        {
            if (a.easing == KeyframeEasing.IN) return a.value + (b.value - a.value) * x * x * x;
            if (a.easing == KeyframeEasing.OUT)
            {
                x -= 1;
                return a.value + (b.value - a.value) * (x * x * x + 1);
            }

            x *= 2;

            if (x < 1D) return a.value + (b.value - a.value) / 2 * x * x * x;

            x -= 2;

            return a.value + (b.value - a.value) / 2 * (x * x * x + 2);
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
            if (a.easing == KeyframeEasing.IN) return a.value + (b.value - a.value) * Math.pow(2, 10 * (x - 1));
            if (a.easing == KeyframeEasing.OUT) return a.value + (b.value - a.value) * (-Math.pow(2, -10 * x) + 1);

            if (x == 0) return a.value;
            if (x == 1) return b.value;

            x *= 2;

            if (x < 1D) return a.value + (b.value - a.value) / 2 * Math.pow(2, 10 * (x - 1));

            x -= 1;

            return a.value + (b.value - a.value) / 2 * (-Math.pow(2, -10 * x) + 2);
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
            x1 = MathHelper.clamp(x1, 0, 1);
            x2 = MathHelper.clamp(x2, 0, 1);

            return Interpolations.bezier(0, y1, y2, 1, Interpolations.bezierX(x1, x2, x, e)) * h + a.value;
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
