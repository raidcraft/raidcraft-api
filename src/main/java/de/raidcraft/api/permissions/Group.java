package de.raidcraft.api.permissions;

import java.util.Set;
import java.util.UUID;

/**
 * @author Silthus
 */
public interface Group {

    // Gets this group's actual displayName
    String getName();

    String getGlobalMasterPermission();

    /**
     * Returns the master permission string for this group on the specified world
     *
     * @param world The displayName of the world we're fetching the permission master for
     * @return A string consisting of this group's displayName and the specified world, prefixed by master "master.[group displayName].[world]"
     */
    String getMasterPermission(String world);

    /**
     * Gets all permissions that are attached to this group.
     *
     * @param world The displayName of the world to fetch the permissions for
     * @return A set of unique permission nodes attached to this group.
     */
    Set<String> getPermissions(String world);

    /**
     * Checks whether this group has the specified permission node on the given world
     *
     * @param node  The permission node
     * @param world The displayName of the world on which we're checking the permission
     * @return true if the group has the permission, otherwise false
     */
    boolean hasPermission(String node, String world);

    /**
     * Adds the specified permission to the list for the specified world. If world is null, adds to the group's global permission list
     *
     * @param world The displayName of the world we're attaching the node to
     * @param node  The displayName of the permission node
     * @return true if the permission was attached successfully, otherwise false
     */
    boolean addPermission(String world, String node);

    // same as above but sets world to null adding the node to all worlds
    boolean addPermission(String node);

    /**
     * Removes the specified permission node from the list for the specified world. If world is null, removes from the group's global list.
     *
     * @param world The displayName of the world we're removing the node from
     * @param node  The displayName of the permission node
     * @return true if the node is removed successfully, otherwise false
     */
    boolean removePermission(String world, String node);

    // same as above but sets world to null removing the node from all worlds
    boolean removePermission(String node);

    /**
     * Checks if the player has the global group permission.
     *
     * @param playerId to check group for
     * @return true if player is in group
     */
    boolean isPlayerInGroup(UUID playerId);

    /**
     * Checks if the player is in this permission group.
     *
     * @param world    to check
     * @param playerId to check group for
     * @return true if player is in group
     */
    boolean isPlayerInGroup(String world, UUID playerId);
}