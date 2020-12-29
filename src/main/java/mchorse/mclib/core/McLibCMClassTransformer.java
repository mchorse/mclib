package mchorse.mclib.core;

import mchorse.mclib.core.transformers.CPacketCustomPayloadTransformer;
import mchorse.mclib.core.transformers.EntityRendererTransformer;
import mchorse.mclib.core.transformers.SimpleReloadableResourceManagerTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;

public class McLibCMClassTransformer extends CoreClassTransformer
{
    private SimpleReloadableResourceManagerTransformer resourcePack = new SimpleReloadableResourceManagerTransformer();
    private CPacketCustomPayloadTransformer customPayload = new CPacketCustomPayloadTransformer();
    private EntityRendererTransformer entityRenderer = new EntityRendererTransformer();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (checkName(name, "cev", "net.minecraft.client.resources.SimpleReloadableResourceManager"))
        {
            System.out.println("McLib: Transforming SimpleReloadableResourceManager class (" + name + ")");

            return this.resourcePack.transform(name, basicClass);
        }
        else if (checkName(name, "lh", "net.minecraft.network.play.client.CPacketCustomPayload"))
        {
            System.out.println("McLib: Transforming CPacketCustomPayloadTransformer class (" + name + ")");

            return this.customPayload.transform(name, basicClass);
        }
        else if (checkName(name, "buq", "net.minecraft.client.renderer.EntityRenderer"))
        {
            System.out.println("McLib: patching EntityRenderer (" + name + ")");

            return this.entityRenderer.transform(name, basicClass);
        }

        return basicClass;
    }
}