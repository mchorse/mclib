package mchorse.mclib.math.functions.utility;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class RandomInteger extends Random
{
    public RandomInteger(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double get()
    {
        return (int) super.get();
    }
}