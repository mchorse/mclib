package mchorse.mclib.utils.resources;

import net.minecraft.util.ResourceLocation;

public interface IResourceTransformer
{
    public String transformDomain(String domain);

    public String transformPath(String path);

    public String transform(String location);
}