package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.player.PlayerResolver;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Silthus
 */
public class UUIDUtil {

    private static PlayerResolver _resolver;

    private static Optional<PlayerResolver> getPlayerResolver() {
        if (_resolver == null) {
            _resolver = RaidCraft.getComponent(PlayerResolver.class);
        }
        return Optional.ofNullable(_resolver);
    }

    public static <T> UUID getUUIDfrom(T type) {

        if (type instanceof Entity) {
            return ((Entity) type).getUniqueId();
        } else {
            return UUID.randomUUID();
        }
    }

    public static UUID convertPlayer(@NonNull String name) {

        return getPlayerResolver()
                .map(playerResolver -> playerResolver.convertPlayer(name))
                .orElseGet(() -> Bukkit.getOfflinePlayer(name).getUniqueId());
    }

    public static String getUUIDStringFromName(String name) {

        UUID uuid = convertPlayer(name);
        return uuid != null ? uuid.toString() : null;
    }

    public static String getNameFromUUID(UUID uuid) {

        if(uuid == null) {
            return null;
        }

        return getPlayerResolver()
                .map(playerResolver -> playerResolver.getNameFromUUID(uuid))
                .orElseGet(() -> Bukkit.getOfflinePlayer(uuid).getName());
    }

    public static String getNameFromUUID(String uuid) {

        return getNameFromUUID(UUID.fromString(uuid));
    }

    public static String castUUID(CommandSender sender) {

        return ((Player) sender).getUniqueId().toString();
    }

    public static int getPlayerId(Player player) {

        return getPlayerId(getUUIDfrom(player));
    }

    public static int getPlayerId(UUID uuid) {

        return getPlayerResolver()
                .map(playerResolver -> playerResolver.getPlayerId(uuid))
                .orElse(0);
    }

    public static UUID getUuidFromPlayerId(int id) {

        return getPlayerResolver()
                .map(playerResolver -> playerResolver.getUuidFromPlayerId(id))
                .orElse(null);
    }
}
