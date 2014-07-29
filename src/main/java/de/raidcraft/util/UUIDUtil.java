package de.raidcraft.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
                throw new Exception("null name for uuid convert");
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    public static String getUUIDStringFromName(String name) {
        UUID uuid = convertPlayer(name);
        return uuid != null ? uuid.toString() : null;
    }

    public static String getNameFromUUID(String uuid) {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
    }

    public static String castUUID(CommandSender sender) {

        return ((Player) sender).getUniqueId().toString();
    }
}
