package mchorse.mclib.math.functions.rounding;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.NNFunction;

public class Ceil extends NNFunction
{
    public Ceil(IValue[] values, String name) throws Exception
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
        return Math.ceil(this.getArg(0).doubleValue());
    }
}