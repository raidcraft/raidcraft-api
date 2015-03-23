package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.tables.TRcPlayer;
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

    public static <T> UUID getUUIDfrom(T type) {

        if (type instanceof Entity) {
            return ((Entity) type).getUniqueId();
        } else {
            return UUID.randomUUID();
        }
    }

    public static UUID convertPlayer(@NonNull String name) {

        TRcPlayer tPlayer = RaidCraft.getComponent(RaidCraftPlugin.class).getDatabase()
                .find(TRcPlayer.class).where().eq("last_name", name).findUnique();
        if (tPlayer != null) {
            return tPlayer.getUuid();
        }
        // if we are here there is no player for this name -> must be a npc
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    public static String getUUIDStringFromName(String name) {

        UUID uuid = convertPlayer(name);
        return uuid != null ? uuid.toString() : null;
    }

    public static String getNameFromUUID(UUID uuid) {

        TRcPlayer tPlayer = RaidCraft.getComponent(RaidCraftPlugin.class).getDatabase()
                .find(TRcPlayer.class).where().eq("uuid", uuid.toString()).findUnique();
        if (tPlayer != null) {
            return tPlayer.getLastName();
        }
        return Bukkit.getOfflinePlayer(uuid).getName();
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

        if (uuid == null) return 0;
        TRcPlayer rcPlayer = RaidCraft.getDatabase(RaidCraftPlugin.class).find(TRcPlayer.class).where().eq("uuid", uuid).findUnique();
        if (rcPlayer != null) {
            return rcPlayer.getId();
        }
        return 0;
    }

    public static Optional<UUID> getUuidFromPlayerId(int id) {

        TRcPlayer rcPlayer = RaidCraft.getDatabase(RaidCraftPlugin.class).find(TRcPlayer.class, id);
        if (rcPlayer != null) {
            return Optional.of(rcPlayer.getUuid());
        }
        return Optional.empty();
    }
}
