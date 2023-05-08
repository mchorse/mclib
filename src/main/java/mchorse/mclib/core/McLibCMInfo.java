package mchorse.mclib.core;

import net.minecraftforge.fml.common.DummyModContainer;

public class McLibCMInfo extends DummyModContainer
{
    @Override
    public String getName()
    {
        return "McLib core mod";
    }

    @Override
    public String getModId()
    {
        return "mclib_core";
    }

    @Override
    public Object getMod()
    {
        return null;
    }

    @Override
    public String getVersion()
    {
        return "@VERSION@";
    }
}