package mchorse.mclib.utils.files;

import java.util.ArrayList;
import java.util.List;

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
            tree.addBackEntry(this.root);
            this.root.entries.add(tree.root);
        }
    }
}