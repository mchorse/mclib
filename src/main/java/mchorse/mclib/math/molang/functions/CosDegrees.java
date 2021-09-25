package mchorse.mclib.math.molang.functions;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.NNFunction;

public class CosDegrees extends NNFunction
{
    public CosDegrees(IValue[] values, String name) throws Exception
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
        return Math.cos(this.getArg(0).doubleValue() / 180 * Math.PI);
    }
}