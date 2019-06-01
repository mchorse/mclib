package mchorse.mclib.math.functions;

import mchorse.mclib.math.IValue;

public class Floor extends Function
{
    public Floor(IValue[] values) throws Exception
    {
        super(values);
    }

    @Override
    public String getName()
    {
        return "floor";
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double get()
    {
        return Math.floor(this.getArg(0));
    }
}