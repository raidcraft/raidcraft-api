package de.raidcraft.api.language;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface TranslationProvider {


    @Deprecated
    String tr(Language lang, String key, Object... args);

    String tr(Language lang, String key, String defaultValue, Object... args);

    @Deprecated
    String tr(CommandSender sender, String key, Object... args);

    String tr(CommandSender sender, String key, String defaultValue, Object... args);

    @Deprecated
    String tr(Player player, String key, Object... args);

    String tr(Player player, String key, String def, Object... args);

    @Deprecated
    String var(Language language, String key);

    @Deprecated
    String var(Language language, String key, String def);

    @Deprecated
    String var(CommandSender sender, String key);

    @Deprecated
    String var(CommandSender sender, String key, String def);

    @Deprecated
    String var(Player player, String key);

    @Deprecated
    String var(Player player, String key, String def);

    @Deprecated
    void msg(CommandSender sender, String key, Object... args);

    void msg(CommandSender sender, String key, String defaultValue, Object... args);

    @Deprecated
    void msg(Player player, String key, Object... args);

    void msg(Player player, String key, String defaultValue, Object... args);

    @Deprecated
    void msg(CommandSender sender, String key, ChatColor color, Object... args);

    void msg(CommandSender sender, String key, ChatColor color, String defaultValue, Object... args);

    @Deprecated
    void msg(Player player, String key, ChatColor color, Object... args);

    void msg(Player player, String key, ChatColor color, String defaultValue, Object... args);

    @Deprecated
    int broadcastMessage(String key, Object... args);

    int broadcastMessage(String key, String defaultValue, Object... args);

    @Deprecated
    int broadcastMessage(String key, ChatColor color, String def, Object... args);

    @Deprecated
    int broadcastMessage(String key, ChatColor color, Object... args);
}
