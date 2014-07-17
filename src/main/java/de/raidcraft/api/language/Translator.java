package de.raidcraft.api.language;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * for params in String:
 * http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html
 * alternativ myplugin.getTranslationProvider().tr(...)
 * @author Silthus
 */
public class Translator {

    public static String tr(Class<? extends BasePlugin> clazz, Language language, String key, Object... args) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        return plugin.getTranslationProvider().tr(language, key, args);
    }

    public static String tr(Class<? extends BasePlugin> clazz, Language language, String key, String def, Object... args) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        return plugin.getTranslationProvider().tr(language, key, def, args);
    }

    public static String tr(Class<? extends BasePlugin> clazz, Player player, String key, Object... args) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        return plugin.getTranslationProvider().tr(player, key, args);
    }

    public static String tr(Class<? extends BasePlugin> clazz, Player player, String key, String def, Object... args) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        return plugin.getTranslationProvider().tr(player, key, def, args);
    }

    public static void msg(Class<? extends BasePlugin> clazz, CommandSender sender, String key, Object... args) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        plugin.getTranslationProvider().msg(sender, key, args);
    }

    public static void msg(Class<? extends BasePlugin> clazz, CommandSender sender, String key, String def, Object... args) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        plugin.getTranslationProvider().msg(sender, key, def, args);
    }

    public static void msg(Class<? extends BasePlugin> clazz, Player player, String key, Object... args) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        plugin.getTranslationProvider().msg(player, key, args);
    }

    public static void msg(Class<? extends BasePlugin> clazz, Player player, String key, String def, Object... args) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        plugin.getTranslationProvider().msg(player, key, def, args);
    }
}
