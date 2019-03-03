package mchorse.mclib.utils.files;

import java.util.Comparator;

import mchorse.mclib.client.gui.framework.elements.GuiTexturePicker;
import mchorse.mclib.utils.files.AbstractEntry.FolderEntry;

/**
 * File tree
 * 
 * The implementations of file tree are responsible for creating full 
 * tree of files, so that {@link GuiTexturePicker} could navigate it.
 */
public abstract class FileTree
{
    /**
     * Abstract entry sorter. Sorts folders on top first, and then by 
     * the display title name   
     */
    public static Comparator<AbstractEntry> SORTER = new EntrySorter();

    /**
     * Root entry, this top folder should be populated in 
     * {@link #rebuild()} method.
     */
    public FolderEntry root = new FolderEntry("root", null);

    /**
     * Does the tree needs a rebuild 
     */
    public boolean needsRebuild = true;

    /**
     * Rebuild the file tree 
     */
    public abstract void rebuild();

    /**
     * Adds a "back to parent directory" entry 
     */
    public void addBackEntry(FolderEntry entry)
    {
        FolderEntry top = new FolderEntry("../", entry);

        top.entries = entry.parent.entries;
        entry.entries.add(top);
    }

    /**
     * Get a top level folder for given name    
     */
    public FolderEntry getEntryForName(String name)
    {
        for (AbstractEntry entry : this.root.entries)
        {
            if (entry instanceof FolderEntry)
            {
                FolderEntry folder = (FolderEntry) entry;

                if (folder.title.equalsIgnoreCase(name))
                {
                    return folder;
                }
            }
        }

        return this.root;
    }

    /**
     * Get a folder entry by path 
     */
    public FolderEntry getByPath(String path)
    {
        return this.getByPath(path, this.root);
    }

    /**
     * Get a folder entry by path with a default value, if given path 
     * wasn't found 
     */
    public FolderEntry getByPath(String path, FolderEntry orDefault)
    {
        FolderEntry entry = this.root;

        for (String segment : path.trim().split("/"))
        {
            for (AbstractEntry folder : entry.entries)
            {
                if (folder.isFolder() && folder.title.equalsIgnoreCase(segment))
                {
                    entry = (FolderEntry) folder;
                }
            }
        }

        return this.root == entry ? orDefault : entry;
    }
}