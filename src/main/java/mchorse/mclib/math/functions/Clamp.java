package mchorse.mclib.math.functions;

import mchorse.mclib.math.IValue;
import net.minecraft.util.math.MathHelper;

public class Clamp extends Function
{
    public Clamp(IValue[] values) throws Exception
    {
        super(values);
    }

    @Override
    public String getName()
    {
        return "clamp";
    }

    @Override
    public int getRequiredArguments()
    {
        return 3;
    }

    @Override
    public double get()
    {
        return MathHelper.clamp(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}