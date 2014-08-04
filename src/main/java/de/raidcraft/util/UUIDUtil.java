package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.tables.TRcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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

    public static UUID convertPlayer(String name) {
        if(name == null) {
            try {
                throw new NullPointerException();
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
        Plugin plugin = RaidCraft.getComponent(RaidCraftPlugin.class);
        TRcPlayer tPlayer = plugin.getDatabase().find(TRcPlayer.class).where().eq("last_name", name).findUnique();
        if(tPlayer != null) {
            return tPlayer.getUuid();
        }
        try {
            throw new Exception("UUID not found for playername: " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    public static String getUUIDStringFromName(String name) {
        UUID uuid = convertPlayer(name);
        return uuid != null ? uuid.toString() : null;
    }

    public static String getNameFromUUID(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    public static String getNameFromUUID(String uuid) {
        return getNameFromUUID(UUID.fromString(uuid));
    }

    public static String castUUID(CommandSender sender) {

        return ((Player) sender).getUniqueId().toString();
    }
}
