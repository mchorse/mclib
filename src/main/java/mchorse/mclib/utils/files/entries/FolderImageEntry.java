package mchorse.mclib.utils.files.entries;

import mchorse.mclib.utils.resources.RLUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;

public class FolderImageEntry extends FolderEntry
{
    public FolderImageEntry(String title, File file, FolderEntry parent)
    {
        super(title, file, parent);
    }

    @Override
    protected void populateEntries()
    {
        String prefix = this.getPrefix();

        for (File file : this.file.listFiles())
        {
            AbstractEntry entry = null;
            String name = file.getName();
            String lowercase = name.toLowerCase();

            if (file.isDirectory())
            {
                entry = new FolderImageEntry(name, file, this);
            }
            else if (file.isFile())
            {
                /* Only textures files should be shown */
                if (lowercase.endsWith(".png") || lowercase.endsWith(".jpg") || lowercase.endsWith(".jpeg") || lowercase.endsWith(".gif"))
                {
                    String path = prefix + (prefix.contains(":") ? "/" : ":") + name;

                    entry = new FileEntry(name, file, RLUtils.create(path));
                }
            }

            if (Objects.equals(this.getEntry(name), entry))
            {
                continue;
            }

            if (entry != null)
            {
                this.entries.add(entry);
            }
        }

        /* Remove removed folders and files */
        Iterator<AbstractEntry> it = this.entries.iterator();

        while (it.hasNext())
        {
            AbstractEntry entry = it.next();

            if (!entry.exists())
            {
                it.remove();
            }
        }

        super.populateEntries();
    }
}