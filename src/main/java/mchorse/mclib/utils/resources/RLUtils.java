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
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
            BufferedImage image = ImageIO.read(manager.getResource(multi.children.get(0)).getInputStream());
            Graphics g = image.getGraphics();

            for (int i = 1; i < multi.children.size(); i++)
            {
                ResourceLocation child = multi.children.get(i);

                try
                {
                    IResource resource = manager.getResource(child);
                    BufferedImage childImage = ImageIO.read(resource.getInputStream());

                    g.drawImage(childImage, 0, 0, null);
                }
                catch (Exception e)
                {}
            }

            g.dispose();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);

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
        if (base instanceof NBTTagList)
        {
            NBTTagList list = (NBTTagList) base;

            if (!list.hasNoTags())
            {
                MultiResourceLocation multi = new MultiResourceLocation(list.getStringTagAt(0));

                for (int i = 1; i < list.tagCount(); i++)
                {
                    multi.children.add(create(list.getStringTagAt(i)));
                }

                return multi;
            }
        }
        else if (base instanceof NBTTagString)
        {
            return create(((NBTTagString) base).getString());
        }

        return null;
    }

    public static ResourceLocation create(JsonElement jsonElement)
    {
        if (jsonElement.isJsonArray())
        {
            JsonArray array = jsonElement.getAsJsonArray();
            int size = array.size();

            if (size > 0)
            {
                JsonElement first = array.get(0);

                if (first.isJsonPrimitive())
                {
                    MultiResourceLocation location = new MultiResourceLocation(first.getAsString());

                    for (int i = 1; i < size; i++)
                    {
                        location.children.add(create(array.get(i)));
                    }

                    return location;
                }
            }
        }
        else if (jsonElement.isJsonPrimitive())
        {
            return create(jsonElement.getAsString());
        }

        return null;
    }

    public static NBTBase writeNbt(ResourceLocation location)
    {
        if (location instanceof MultiResourceLocation)
        {
            MultiResourceLocation multi = (MultiResourceLocation) location;
            NBTTagList list = new NBTTagList();

            for (ResourceLocation child : multi.children)
            {
                list.appendTag(new NBTTagString(child.toString()));
            }

            return list;
        }
        else if (location != null)
        {
            return new NBTTagString(location.toString());
        }

        return null;
    }

    public static JsonElement writeJson(ResourceLocation location)
    {
        if (location instanceof MultiResourceLocation)
        {
            MultiResourceLocation multi = (MultiResourceLocation) location;
            JsonArray array = new JsonArray();

            for (ResourceLocation child : multi.children)
            {
                array.add(new JsonPrimitive(child.toString()));
            }

            return array;
        }
        else if (location != null)
        {
            return new JsonPrimitive(location.toString());
        }

        return JsonNull.INSTANCE;
    }

    public static ResourceLocation clone(ResourceLocation location)
    {
        if (location instanceof MultiResourceLocation)
        {
            MultiResourceLocation multi = (MultiResourceLocation) location;
            MultiResourceLocation newMulti = new MultiResourceLocation(multi.toString());

            newMulti.children.clear();
            newMulti.children.addAll(multi.children);

            return newMulti;
        }
        else if (location != null)
        {
            return create(location.toString());
        }

        return null;
    }
}