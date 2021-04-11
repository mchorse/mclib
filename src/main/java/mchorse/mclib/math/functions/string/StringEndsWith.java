package mchorse.mclib.math.functions.string;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;

public class StringEndsWith extends SNFunction
{
    public StringEndsWith(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 2;
    }

    @Override
    public double doubleValue()
    {
        return this.getArg(0).stringValue().endsWith(this.getArg(1).stringValue()) ? 1 : 0;
    }
}
