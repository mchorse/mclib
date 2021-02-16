package mchorse.mclib.utils.keyframes;

import com.google.gson.annotations.Expose;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IByteBufSerializable;
import mchorse.mclib.network.INBTSerializable;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Keyframe class
 *
 * This class is responsible for storing individual keyframe properties such
 * as tick at which its located, value, interpolation, easing type, etc.
 */
public class Keyframe implements IByteBufSerializable, INBTSerializable
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
        this();

        this.tick = tick;
        this.value = value;
    }

    public Keyframe()
    {
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

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.tick = buffer.readLong();
        this.value = buffer.readDouble();
        this.interp = KeyframeInterpolation.values()[buffer.readInt()];
        this.easing = KeyframeEasing.values()[buffer.readInt()];
        this.rx = buffer.readFloat();
        this.ry = buffer.readFloat();
        this.lx = buffer.readFloat();
        this.ly = buffer.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buffer)
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

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Tick")) this.tick = tag.getLong("Tick");
        if (tag.hasKey("Value")) this.value = tag.getDouble("Value");
        if (tag.hasKey("Interp")) this.interp = KeyframeInterpolation.values()[tag.getInteger("Interp")];
        if (tag.hasKey("Easing")) this.easing = KeyframeEasing.values()[tag.getInteger("Easing")];
        if (tag.hasKey("RX")) this.rx = tag.getFloat("RX");
        if (tag.hasKey("RY")) this.ry = tag.getFloat("RY");
        if (tag.hasKey("LX")) this.lx = tag.getFloat("LX");
        if (tag.hasKey("LY")) this.ly = tag.getFloat("LY");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tag)
    {
        tag.setLong("Tick", this.tick);
        tag.setDouble("Value", this.value);

        if (this.interp != KeyframeInterpolation.LINEAR) tag.setInteger("Interp", this.interp.ordinal());
        if (this.easing != KeyframeEasing.IN) tag.setInteger("Easing", this.easing.ordinal());
        if (this.rx != 5) tag.setFloat("RX", this.rx);
        if (this.ry != 0) tag.setFloat("RY", this.ry);
        if (this.lx != 5) tag.setFloat("LX", this.lx);
        if (this.ly != 0) tag.setFloat("LY", this.ly);

        return tag;
    }
}
