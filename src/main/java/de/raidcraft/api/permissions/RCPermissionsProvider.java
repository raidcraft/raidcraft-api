package de.raidcraft.api.permissions;

import de.raidcraft.api.BasePlugin;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Defines a provider that takes care of the saving and loading of permission nodes.
 * The provider needs the be registered with the PermissionsPlugin
 * and then is queried when the Plugin loads.
 *
 * @author Silthus
 */
public interface RCPermissionsProvider<T extends BasePlugin> {

    T getPlugin();

    // gets the registered default group that needs to be used
    Group getDefaultGroup();

    /**
     * Gets a list of groups provided by the PermissionsProvider. All underlying
     * saving and storing of those groups is taken care of by the provider.
     *
     * @return List of constructed groups that should be made available for all players.
     */
    List<Group> getGroups();

    /**
     * Gets a list of all the groups a player belongs to. Needs to return the groups
     * even if the player is not logged in.
     *
     * @param player to get the groups for
     * @return Groups of the player
     */
    Set<String> getPlayerGroups(UUID player);
}
