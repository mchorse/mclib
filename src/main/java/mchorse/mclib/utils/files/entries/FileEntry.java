package mchorse.mclib.utils.files.entries;

import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.Objects;

public class FileEntry extends AbstractEntry
{
    public ResourceLocation resource;

    public FileEntry(String title, File file, ResourceLocation resource)
    {
        super(title, file);

        this.resource = resource;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof FileEntry)
        {
            result = result && Objects.equals(this.resource, ((FileEntry) obj).resource);
        }

        return result;
    }
}
