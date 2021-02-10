package mchorse.mclib.math.functions.utility;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class DieRoll extends Function
{
    public static double rollDie(int num, double min, double max)
    {
        double m = Math.max(max, min);
        double n = Math.min(max, min);

        double sum = 0;

        for (int i = 0; i < num; i++)
        {
            sum += Math.random() * (m - n) + n;
        }

        return sum;
    }

    public DieRoll(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 3;
    }

    @Override
    public double get()
    {
        return rollDie((int) this.getArg(0), this.getArg(1), this.getArg(2));
    }
}