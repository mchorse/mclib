package mchorse.mclib.permissions;

import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class McLibPermissionsRegistry extends PermissionRegistry
{
    public final PermissionCategory configEdit = new PermissionCategory("edit_config", DefaultPermissionLevel.OP);
    public final PermissionCategory accessGui = new PermissionCategory("access_gui");

    public McLibPermissionsRegistry(String modid)
    {
        /* categories */
        PermissionCategory root = new PermissionCategory(modid);
        PermissionCategory gui = new PermissionCategory("gui", DefaultPermissionLevel.OP);

        root.addChild(gui);

        /* add permissions */
        this.addPermission(root, this.configEdit);
        this.addPermission(gui, this.accessGui);
    }
}
