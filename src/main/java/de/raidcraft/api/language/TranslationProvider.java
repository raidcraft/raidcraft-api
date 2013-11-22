package de.raidcraft.api.language;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface TranslationProvider {

    public String tr(Language lang, String key);

    public String tr(Language lang, String key, String def);

    public String tr(Player player, String key);

    public String tr(Player player, String key, String def);
}
