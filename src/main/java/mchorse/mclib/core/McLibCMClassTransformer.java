package mchorse.mclib.core;

import mchorse.mclib.core.transformers.SimpleReloadableResourceManagerTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;

public class McLibCMClassTransformer extends CoreClassTransformer
{
    private SimpleReloadableResourceManagerTransformer resourcePack = new SimpleReloadableResourceManagerTransformer();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (checkName(name, "bxi", "net.minecraft.client.resources.SimpleReloadableResourceManager"))
        {
            System.out.println("McLib: Transforming SimpleReloadableResourceManager class (" + name + ")");

            return this.resourcePack.transform(name, basicClass);
        }

        return basicClass;
    }
}