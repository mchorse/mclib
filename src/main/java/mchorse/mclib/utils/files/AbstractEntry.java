package mchorse.mclib.utils.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

/**
 * Abstract file tree entry class
 * 
 * This basic type is basically contained within file tree
 */
public abstract class AbstractEntry
{
    /**
     * Displayable title 
     */
    public String title;

    public AbstractEntry(String title)
    {
        this.title = title;
    }

    public boolean isFolder()
    {
        return this instanceof FolderEntry;
    }

    public static class FolderEntry extends AbstractEntry
    {
        public File file;
        public FolderEntry parent;
        public List<AbstractEntry> entries = new ArrayList<AbstractEntry>();

        public FolderEntry(String title, FolderEntry parent)
        {
            super(title);

            this.parent = parent;
        }

        public FolderEntry(String title, FolderEntry parent, File file)
        {
            this(title, parent);

            this.file = file;
        }

        public boolean isTop()
        {
            return this.parent != null && this.parent.parent != null && this.parent.parent.entries == this.entries;
        }
    }

    public static class FileEntry extends AbstractEntry
    {
        public ResourceLocation resource;

        public FileEntry(String title, ResourceLocation resource)
        {
            super(title);

            this.resource = resource;
        }
    }
}