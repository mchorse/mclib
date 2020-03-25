package mchorse.mclib.math.functions.limit;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.util.math.MathHelper;

public class Clamp extends Function
{
    public Clamp(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 3;
    }

    @Override
    public double get()
    {
        return MathUtils.clamp(this.getArg(0), this.getArg(1), this.getArg(2));
    }
}