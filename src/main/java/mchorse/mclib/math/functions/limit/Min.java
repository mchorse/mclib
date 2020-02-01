package mchorse.mclib.math.functions.limit;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class Min extends Function
{
	public Min(IValue[] values, String name) throws Exception
	{
		super(values, name);
	}

	@Override
	public int getRequiredArguments()
	{
		return 2;
	}

	@Override
	public double get()
	{
		return Math.min(this.getArg(0), this.getArg(1));
	}
}