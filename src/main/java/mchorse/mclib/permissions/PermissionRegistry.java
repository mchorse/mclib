package mchorse.mclib.permissions;

import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;
import java.util.List;

public abstract class PermissionRegistry
{
    /**
     * List of all permissions (the ends of the tree structure)
     */
    private final List<PermissionCategory> permissions = new ArrayList<>();

    /**
     * Add the permission to the provided category and to the internal permission list
     * so they can later be registered to the forge API.
     * @param category
     * @param permission
     */
    protected void addPermission(PermissionCategory category, PermissionCategory permission)
    {
        category.addChild(permission);
        this.permissions.add(permission);
    }

    /**
     * Register the permissions to the forge API {@link PermissionAPI}
     */
    public void registerPermissions()
    {
        for (PermissionCategory permission : this.permissions)
        {
            if (permission.hasChildren()) continue;

            PermissionAPI.registerNode(permission.toString(), permission.getDefaultPermission(), "");
        }
    }
}
