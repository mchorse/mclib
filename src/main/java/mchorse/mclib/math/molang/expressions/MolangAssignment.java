package mchorse.mclib.math.molang.expressions;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.Variable;
import mchorse.mclib.math.molang.MolangParser;

public class MolangAssignment extends MolangExpression
{
    public Variable variable;
    public IValue expression;

    public MolangAssignment(MolangParser context, Variable variable, IValue expression)
    {
        super(context);

        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public double get()
    {
        double value = this.expression.get().doubleValue();

        this.variable.set(value);

        return value;
    }

    @Override
    public String toString()
    {
        return this.variable.getName() + " = " + this.expression.toString();
    }
}