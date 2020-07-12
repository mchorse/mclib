package mchorse.mclib.utils.keyframes;

import com.google.gson.annotations.Expose;
import io.netty.buffer.ByteBuf;

/**
 * Keyframe class
 *
 * This class is responsible for storing individual keyframe properties such
 * as tick at which its located, value, interpolation, easing type, etc.
 */
public class Keyframe
{
    public Keyframe prev;
    public Keyframe next;

    @Expose
    public long tick;

    @Expose
    public double value;

    @Expose
    public KeyframeInterpolation interp = KeyframeInterpolation.LINEAR;

    @Expose
    public KeyframeEasing easing = KeyframeEasing.IN;

    @Expose
    public float rx = 5;

    @Expose
    public float ry;

    @Expose
    public float lx = 5;

    @Expose
    public float ly;

    public Keyframe(long tick, double value)
    {
        this.tick = tick;
        this.value = value;

        this.prev = this;
        this.next = this;
    }

    public void setTick(long tick)
    {
        this.tick = tick;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public void setInterpolation(KeyframeInterpolation interp)
    {
        this.interp = interp;
    }

    public void setInterpolation(KeyframeInterpolation interp, KeyframeEasing easing)
    {
        this.interp = interp;
        this.setEasing(easing);
    }

    public void setEasing(KeyframeEasing easing)
    {
        this.easing = easing;
    }

    public double interpolate(Keyframe frame, float x)
    {
        return this.interp.interpolate(this, frame, x);
    }

    public Keyframe copy()
    {
        Keyframe frame = new Keyframe(this.tick, this.value);

        frame.copy(this);

        return frame;
    }

    public void copy(Keyframe keyframe)
    {
        this.tick = keyframe.tick;
        this.value = keyframe.value;
        this.interp = keyframe.interp;
        this.easing = keyframe.easing;
        this.lx = keyframe.lx;
        this.ly = keyframe.ly;
        this.rx = keyframe.rx;
        this.ry = keyframe.ry;
    }

    public void fromByteBuf(ByteBuf buffer)
    {
        this.interp = KeyframeInterpolation.values()[buffer.readInt()];
        this.easing = KeyframeEasing.values()[buffer.readInt()];
        this.rx = buffer.readFloat();
        this.ry = buffer.readFloat();
        this.lx = buffer.readFloat();
        this.ly = buffer.readFloat();
    }

    public void toByteBuf(ByteBuf buffer)
    {
        buffer.writeLong(this.tick);
        buffer.writeDouble(this.value);
        buffer.writeInt(this.interp.ordinal());
        buffer.writeInt(this.easing.ordinal());
        buffer.writeFloat(this.rx);
        buffer.writeFloat(this.ry);
        buffer.writeFloat(this.lx);
        buffer.writeFloat(this.ly);
    }
}
