package mchorse.mclib.permissions;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mchorse.mclib.McLib;

import javax.annotation.Nullable;

/**
 * This class is needed for serialisation, for example, into bytes to reduce the amount of data being sent.
 */
public class PermissionFactory
{
    /**
     * Map the permission string hashcode to the permission
     */
    private final BiMap<Integer, PermissionCategory> permissions = HashBiMap.create();

    public void registerPermission(PermissionCategory permission)
    {
        String name = permission.toString();
        int hash = name.hashCode();

        if (this.permissions.containsKey(hash))
        {
            McLib.LOGGER.warn("The hash of the permission " + name + " is equal to the already registered permission " + this.permissions.get(hash));

            return;
        }

        if (!permission.hasChildren() && !this.permissions.containsValue(permission))
        {
            this.permissions.put(hash, permission);
        }
    }

    public boolean isRegistered(PermissionCategory permission)
    {
        return this.permissions.containsValue(permission);
    }

    /**
     * @param permission
     * @return the id of the permission or -1 if the provided permission was not registered
     */
    public int getPermissionID(PermissionCategory permission)
    {
        return this.permissions.inverse().get(permission);
    }

    @Nullable
    public PermissionCategory getPermission(int id)
    {
        return this.permissions.get(id);
    }
}
