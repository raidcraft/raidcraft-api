package de.raidcraft.api.language;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface TranslationProvider {


    String tr(Language lang, String key, Object... args);

    String tr(Language lang, String key, String def, Object... args);

    String tr(CommandSender sender, String key, Object... args);

    String tr(CommandSender sender, String key, String def, Object... args);

    String tr(Player player, String key, Object... args);

    String tr(Player player, String key, String def, Object... args);

    String var(Language language, String key);

    String var(Language language, String key, String def);

    String var(CommandSender sender, String key);

    String var(CommandSender sender, String key, String def);

    String var(Player player, String key);

    String var(Player player, String key, String def);

    void msg(CommandSender sender, String key, Object... args);

    void msg(CommandSender sender, String key, String def, Object... args);

    void msg(Player player, String key, Object... args);

    void msg(Player player, String key, String def, Object... args);

    void msg(CommandSender sender, String key, ChatColor color, Object... args);

    void msg(CommandSender sender, String key, ChatColor color, String def, Object... args);

    void msg(Player player, String key, ChatColor color, Object... args);

    void msg(Player player, String key, ChatColor color, String def, Object... args);

    int broadcastMessage(String key, Object... args);

    int broadcastMessage(String key, String def, Object... args);

    int broadcastMessage(String key, ChatColor color, String def, Object... args);

    int broadcastMessage(String key, ChatColor color, Object... args);
}
