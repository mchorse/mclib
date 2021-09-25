package mchorse.mclib.math.molang.expressions;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.molang.MolangParser;

public class MolangValue extends MolangExpression
{
    public IValue value;
    public boolean returns;

    public MolangValue(MolangParser context, IValue value)
    {
        super(context);

        this.value = value;
    }

    public MolangExpression addReturn()
    {
        this.returns = true;

        return this;
    }

    @Override
    public double get()
    {
        return this.value.get().doubleValue();
    }

    @Override
    public String toString()
    {
        return (this.returns ? MolangParser.RETURN : "") + this.value.toString();
    }

    @Override
    public JsonElement toJson()
    {
        if (this.value instanceof Constant)
        {
            return new JsonPrimitive(this.value.get().doubleValue());
        }

        return super.toJson();
    }
}