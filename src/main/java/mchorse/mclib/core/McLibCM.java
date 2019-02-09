package mchorse.mclib.core;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@Name("McLib core mod")
@MCVersion("1.10.2")
@SortingIndex(1)
public class McLibCM implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {McLibCMClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass()
    {
        return McLibCMInfo.class.getName();
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {}

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}