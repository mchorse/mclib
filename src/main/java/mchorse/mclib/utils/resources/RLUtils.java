package mchorse.mclib.utils.resources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * {@link ResourceLocation} utility methods
 *
 * This class has utils for saving and reading {@link ResourceLocation} from
 * actor model and skin.
 */
public class RLUtils
{
    private static List<IResourceTransformer> transformers = new ArrayList<IResourceTransformer>();

    /**
     * Get stream for multi resource location 
     */
    @SideOnly(Side.CLIENT)
    public static IResource getStreamForMultiskin(MultiResourceLocation multi) throws IOException
    {
        if (multi.children.isEmpty())
        {
            throw new IOException("Multi-skin is empty!");
        }

        try
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(TextureProcessor.process(multi), "png", stream);

            return new SimpleResource("McLib multiskin handler", multi, new ByteArrayInputStream(stream.toByteArray()), null, null);
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage());
        }
    }

    public static void register(IResourceTransformer transformer)
    {
        transformers.add(transformer);
    }

    public static ResourceLocation create(String path)
    {
        for (IResourceTransformer transformer : transformers)
        {
            path = transformer.transform(path);
        }

        return new TextureLocation(path);
    }

    public static ResourceLocation create(String domain, String path)
    {
        for (IResourceTransformer transformer : transformers)
        {
            String newDomain = transformer.transformDomain(domain, path);
            String newPath = transformer.transformPath(domain, path);

            domain = newDomain;
            path = newPath;
        }

        return new TextureLocation(domain, path);
    }

    public static ResourceLocation create(NBTBase base)
    {
        ResourceLocation location = MultiResourceLocation.from(base);

        if (location != null)
        {
            return location;
        }

        location = FilteredResourceLocation.from(base);

        if (location != null)
        {
            return location;
        }

        if (base instanceof NBTTagString)
        {
            return create(((NBTTagString) base).getString());
        }

        return null;
    }

    public static ResourceLocation create(JsonElement element)
    {
        ResourceLocation location = MultiResourceLocation.from(element);

        if (location != null)
        {
            return location;
        }

        location = FilteredResourceLocation.from(element);

        if (location != null)
        {
            return location;
        }

        if (element.isJsonPrimitive())
        {
            return create(element.getAsString());
        }

        return null;
    }

    public static NBTBase writeNbt(ResourceLocation location)
    {
        if (location instanceof IWritableLocation)
        {
            return ((IWritableLocation) location).writeNbt();
        }
        else if (location != null)
        {
            return new NBTTagString(location.toString());
        }

        return null;
    }

    public static JsonElement writeJson(ResourceLocation location)
    {
        if (location instanceof IWritableLocation)
        {
            return ((IWritableLocation) location).writeJson();
        }
        else if (location != null)
        {
            return new JsonPrimitive(location.toString());
        }

        return JsonNull.INSTANCE;
    }

    public static ResourceLocation clone(ResourceLocation location)
    {
        if (location instanceof IWritableLocation)
        {
            return ((IWritableLocation) location).clone();
        }
        else if (location != null)
        {
            return create(location.toString());
        }

        return null;
    }
}