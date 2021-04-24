package mchorse.mclib.math.functions;

import mchorse.mclib.math.IValue;

/**
 * Function that expects string input arguments and outputs a string
 */
public abstract class SSFunction extends Function
{
    public SSFunction(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    protected void verifyArgument(int index, IValue value)
    {
        if (value.isNumber())
        {
            throw new IllegalStateException("Function " + this.name + " cannot receive number arguments!");
        }
    }

    @Override
    public IValue get()
    {
        this.result.set(this.stringValue());

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
    public double doubleValue()
    {
        return 0;
    }

    @Override
    public boolean booleanValue()
    {
        return this.stringValue().equalsIgnoreCase("true");
    }
}
