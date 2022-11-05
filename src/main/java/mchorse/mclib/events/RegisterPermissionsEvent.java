package mchorse.mclib.events;

import mchorse.mclib.McLib;
import mchorse.mclib.permissions.PermissionCategory;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;
import java.util.List;

public class RegisterPermissionsEvent extends Event
{
    /**
     * List of the mods registered
     */
    private final List<PermissionCategory> mods = new ArrayList<>();
    /**
     * List of all permissions registered for fast access.
     */
    private final List<PermissionCategory> permissions = new ArrayList<>();

    private PermissionCategory currentMod;
    private PermissionCategory currentCategory;

    /**
     * Register a mod that holds permissions. This is necessary to register permissions or sub categories.
     * @param modid
     * @param level the default permission level.
     *              See {@link PermissionCategory#getDefaultPermission()} for implementation details how a level is inherited.
     */
    public void registerMod(String modid, DefaultPermissionLevel level)
    {
        this.currentMod = new PermissionCategory(modid, level);
        this.currentCategory = this.currentMod;

        this.mods.add(this.currentMod);
    }

    /**
     * register the provided permission category to the current category.
     * @param category
     * @throws UnsupportedOperationException if no mod category was registered
     */
    public void registerCategory(PermissionCategory category) throws UnsupportedOperationException
    {
        if (this.currentMod == null) throw new UnsupportedOperationException("No mod permission category has been registered!");

        this.currentCategory.addChild(category);
        this.currentCategory = category;
    }

    /**
     * Register the permission at the last registered category
     * @param permission
     * @throws UnsupportedOperationException if no category is present to add the permission to
     * @return the registered permission
     */
    public PermissionCategory registerPermission(PermissionCategory permission) throws UnsupportedOperationException
    {
        if (this.currentCategory == null) throw new UnsupportedOperationException("No current category present to add the permission to!");

        this.currentCategory.addChild(permission);
        this.permissions.add(permission);

        return permission;
    }

    /**
     * Register the permissions in the API, see {@link PermissionAPI#registerNode(String, DefaultPermissionLevel, String)}
     * and register them into the permission factory {@link mchorse.mclib.McLib#permissionFactory}.
     */
    public void loadPermissions()
    {
        for (PermissionCategory permission : this.permissions)
        {
            if (permission.hasChildren()) continue;

            PermissionAPI.registerNode(permission.toString(), permission.getDefaultPermission(), "");
            McLib.permissionFactory.registerPermission(permission);
        }
    }

    /**
     * End the current registered category and return to the parent category, so new permissions can be registered.
     * If the current category is the mod category, then the mod category will be ended and so the registering process.
     */
    public void endCategory()
    {
        if (this.currentCategory == this.currentMod)
        {
            this.currentMod = null;
            this.currentCategory = null;
        }
        else
        {
            this.currentCategory = this.currentCategory.getParent();
        }
    }
}
