package mchorse.mclib.math.functions;

import mchorse.mclib.math.IValue;

/**
 * Absolute value function 
 */
public class Abs extends Function
{
    public Abs(IValue[] values) throws Exception
    {
        super(values);
    }

    @Override
    public String getName()
    {
        return "abs";
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double get()
    {
        return Math.abs(this.getArg(0));
    }
}