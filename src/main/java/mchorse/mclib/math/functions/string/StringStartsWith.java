package mchorse.mclib.math.functions.string;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;

public class StringStartsWith extends SNFunction
{
    public StringStartsWith(IValue[] values, String name) throws Exception
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
        return this.getArg(0).stringValue().startsWith(this.getArg(1).stringValue()) ? 1 : 0;
    }
}
