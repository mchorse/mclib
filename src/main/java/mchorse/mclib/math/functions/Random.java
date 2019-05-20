package mchorse.mclib.math.functions;

import mchorse.mclib.math.IValue;

public class Random extends Function
{
    public Random(IValue[] values) throws Exception
    {
        super(values);
    }

    @Override
    public double get()
    {
        double random = Math.random();

        if (this.args.length >= 2)
        {
            double a = this.args[0].get();
            double b = this.args[1].get();

            double min = Math.min(a, b);
            double max = Math.max(a, b);

            random = random * (max - min) + min;
        }
        else if (this.args.length >= 1)
        {
            random = random * this.args[0].get();
        }

        return random;
    }

    @Override
    public String getName()
    {
        return "random";
    }
}