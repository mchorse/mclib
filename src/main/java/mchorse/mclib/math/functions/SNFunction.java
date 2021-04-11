package mchorse.mclib.math.functions;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.Operation;

/**
 * Function that expects string input arguments and outputs a number
 */
public abstract class SNFunction extends Function
{
    public SNFunction(IValue[] values, String name) throws Exception
    {
        super(values, name);

        for (IValue value : values)
        {
            if (value.isNumber())
            {
                throw new IllegalStateException("Function " + name + " cannot receive number arguments!");
            }
        }
    }

    @Override
    public IValue get()
    {
        this.result.set(this.doubleValue());

        return this.result;
    }

    @Override
    public boolean isNumber()
    {
        return false;
    }

    @Override
    public void set(double value)
    {}

    @Override
    public void set(String value)
    {}

    @Override
    public boolean booleanValue()
    {
        return Operation.isTrue(this.doubleValue());
    }

    @Override
    public String stringValue()
    {
        return "";
    }
}
