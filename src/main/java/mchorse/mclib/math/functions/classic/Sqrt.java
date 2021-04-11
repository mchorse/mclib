package mchorse.mclib.math.functions.classic;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.NNFunction;

public class Sqrt extends NNFunction
{
    public Sqrt(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double doubleValue()
    {
        return Math.sqrt(this.getArg(0).doubleValue());
    }
}