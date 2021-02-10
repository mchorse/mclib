package mchorse.mclib.math.functions.trig;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class Atan2 extends Function
{
    public Atan2(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 2;
    }

    @Override
    public double get()
    {
        return Math.atan2(this.getArg(0), this.getArg(1));
    }
}
