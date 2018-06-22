package de.raidcraft.api.permissions;

import de.raidcraft.api.RaidCraftException;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface GroupManager {
    Group createGroup(RCPermissionsProvider provider,
                      String name,
                      Map<String,
                              Set<String>> permissions,
                      String... globalPermissions);

    void updateGroupPermissions(Group group);

    void clean();

    void reload() throws RaidCraftException;

    Group getDefaultGroup();

    Group addPlayerToGroup(UUID playerId, String group);

    Group removePlayerFromGroup(UUID playerId, String group);

    boolean isPlayerInGroup(String world, UUID playerId, String group);

    boolean isPlayerInGroup(UUID playerId, String group);

    Group getGroup(String group);

    Set<Group> getGroups();
}
