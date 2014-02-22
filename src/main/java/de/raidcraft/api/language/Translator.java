package de.raidcraft.api.language;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import org.bukkit.entity.Player;

/**
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
}
