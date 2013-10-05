package de.raidcraft.api.language;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class Translator {

    public static String tr(Class<? extends BasePlugin> clazz, Language language, String key) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        return plugin.getTranslationProvider().tr(language, key);
    }

    public static String tr(Class<? extends BasePlugin> clazz, Player player, String key) {

        BasePlugin plugin = RaidCraft.getComponent(clazz);
        return plugin.getTranslationProvider().tr(player, key);
    }
}
