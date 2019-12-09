package mchorse.mclib.utils.resources;

import net.minecraft.util.ResourceLocation;

public interface IResourceTransformer
{
    public String transformDomain(String domain, String path);

    public String transformPath(String domain, String path);

    public String transform(String location);
}