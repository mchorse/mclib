package mchorse.mclib.math.functions.trig;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class Acos extends Function
{
    public Acos(IValue[] values, String name) throws Exception
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
        return Math.acos(this.getArg(0));
    }
}
