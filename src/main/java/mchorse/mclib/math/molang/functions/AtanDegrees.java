package mchorse.mclib.math.molang.functions;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.trig.Atan;

public class AtanDegrees extends Atan
{
    public AtanDegrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return super.doubleValue() / Math.PI * 180;
    }
}