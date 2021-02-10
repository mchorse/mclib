package mchorse.mclib.math.functions.trig;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class Atan extends Function
{
    public Atan(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double get()
    {
        return Math.atan(this.getArg(0));
    }
}
