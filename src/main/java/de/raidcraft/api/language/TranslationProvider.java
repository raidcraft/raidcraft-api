package de.raidcraft.api.language;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface TranslationProvider {

    public String tr(Language lang, String key, Object... args);

    public String tr(Language lang, String key, String def, Object... args);

    public String tr(CommandSender sender, String key, Object... args);

    public String tr(CommandSender sender, String key, String def, Object... args);

    public String tr(Player player, String key, Object... args);

    public String tr(Player player, String key, String def, Object... args);
}
