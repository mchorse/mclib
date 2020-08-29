package mchorse.mclib.utils.resources;

import com.google.common.base.Objects;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple resource location class
 * 
 * This bad boy allows constructing a single texture out of several 
 * {@link ResourceLocation}s. It doesn't really make sense for other 
 * types of resources beside pictures.
 */
public class MultiResourceLocation extends ResourceLocation implements IWritableLocation
{
    public List<ResourceLocation> children = new ArrayList<ResourceLocation>();

    public static MultiResourceLocation from(NBTBase nbt)
    {
        NBTTagList list = nbt instanceof NBTTagList ? (NBTTagList) nbt : null;

        if (list == null || list.hasNoTags())
        {
            return null;
        }

        MultiResourceLocation multi = new MultiResourceLocation();

        try
        {
            multi.fromNbt(nbt);

            return multi;
        }
        catch (Exception e)
        {}

        return null;
    }

    public static MultiResourceLocation from(JsonElement element)
    {
        JsonArray list = element.isJsonArray() ? (JsonArray) element : null;

        if (list == null || list.size() == 0)
        {
            return null;
        }

        MultiResourceLocation multi = new MultiResourceLocation();

        try
        {
            multi.fromJson(element);

            return multi;
        }
        catch (Exception e)
        {}

        return null;
    }

    public MultiResourceLocation(String resourceName)
    {
        this();
        this.children.add(RLUtils.create(resourceName));
    }

    public MultiResourceLocation(String resourceDomainIn, String resourcePathIn)
    {
        this();
        this.children.add(RLUtils.create(resourceDomainIn, resourcePathIn));
    }

    public MultiResourceLocation()
    {
        /* This needed so there would less chances to match with an
         * actual ResourceLocation */
        super("it_would_be_very_ironic", "if_this_would_match_with_regular_rls");
    }

    @Override
    public String getResourceDomain()
    {
        return this.children.isEmpty() ? "" : this.children.get(0).getResourceDomain();
    }

    @Override
    public String getResourcePath()
    {
        return this.children.isEmpty() ? "" : this.children.get(0).getResourcePath();
    }

    /**
     * This is mostly for looks, but it doesn't really makes sense by  
     * itself
     */
    @Override
    public String toString()
    {
        return this.getResourceDomain() + ":" + this.getResourcePath();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MultiResourceLocation)
        {
            MultiResourceLocation multi = (MultiResourceLocation) obj;

            boolean same = this.children.size() == multi.children.size();

            if (same)
            {
                for (int i = 0, c = this.children.size(); i < c; i++)
                {
                    same = same && Objects.equal(this.children.get(i), multi.children.get(i));
                }
            }

            return same;
        }

        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        int hash = super.hashCode();

        for (int i = 0, c = this.children.size(); i < c; i++)
        {
            hash = 31 * hash + this.children.get(i).hashCode();
        }

        return hash;
    }

    @Override
    public void fromNbt(NBTBase nbt) throws Exception
    {
        NBTTagList list = (NBTTagList) nbt;

        for (int i = 0; i < list.tagCount(); i++)
        {
            ResourceLocation location = RLUtils.create(list.get(i));

            if (location != null)
            {
                this.children.add(location);
            }
        }
    }

    @Override
    public void fromJson(JsonElement element) throws Exception
    {
        JsonArray array = (JsonArray) element;

        for (int i = 0; i < array.size(); i++)
        {
            ResourceLocation location = RLUtils.create(array.get(i));

            if (location != null)
            {
                this.children.add(location);
            }
        }
    }

    @Override
    public NBTBase writeNbt()
    {
        NBTTagList list = new NBTTagList();

        for (ResourceLocation child : this.children)
        {
            NBTBase tag = RLUtils.writeNbt(child);

            if (tag != null)
            {
                list.appendTag(tag);
            }
        }

        return list;
    }

    @Override
    public JsonElement writeJson()
    {
        JsonArray array = new JsonArray();

        for (ResourceLocation child : this.children)
        {
            JsonElement element = RLUtils.writeJson(child);

            if (element != null)
            {
                array.add(element);
            }
        }

        return array;
    }

    @Override
    public ResourceLocation clone()
    {
        MultiResourceLocation newMulti = new MultiResourceLocation();

        for (ResourceLocation child : this.children)
        {
            if (child instanceof IWritableLocation)
            {
                newMulti.children.add(((IWritableLocation) child).clone());
            }
            else
            {
                newMulti.children.add(RLUtils.create(child.toString()));
            }
        }

        return newMulti;
    }
}