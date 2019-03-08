package mchorse.mclib.utils.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mchorse.mclib.utils.files.AbstractEntry.FolderEntry;

/**
 * Global file tree 
 */
public class GlobalTree extends FileTree
{
    /**
     * You can register your own file trees
     */
    public final static GlobalTree TREE = new GlobalTree();

    /**
     * Registered trees
     */
    public List<FileTree> trees = new ArrayList<FileTree>();

    @Override
    public void needsRebuild()
    {
        super.needsRebuild();

        for (FileTree tree : this.trees)
        {
            tree.needsRebuild();
        }
    }

    @Override
    public void rebuild()
    {
        if (!this.needsRebuild)
        {
            return;
        }

        this.root.entries.clear();

        for (FileTree tree : this.trees)
        {
            tree.rebuild();
            this.root.entries.add(tree.root);

            /* Add back to global tree root */
            FolderEntry top = new FolderEntry("../", this.root);

            top.entries = this.root.entries;
            tree.root.entries.add(0, top);
        }

        Collections.sort(this.root.entries, FileTree.SORTER);
    }
}