package de.raidcraft.api.language;

import de.raidcraft.api.BasePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public class ConfigTranslationProvider implements TranslationProvider {

    private final BasePlugin plugin;
    private final Map<Language, TranslationConfig<?>> loadedConfigs = new HashMap<>();
    private final File folder;

    public ConfigTranslationProvider(BasePlugin plugin) {

        this.plugin = plugin;
        folder = new File(plugin.getDataFolder(), "languages/");
        folder.mkdirs();
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                Language language = Language.fromString(file.getName().replace(".yml", ""));
                if (language != null) {
                    loadedConfigs.put(language, plugin.configure(new TranslationConfig<>(plugin, file)));
                }
            }
        }
    }

    private void createConfig(Language lang) {

        TranslationConfig<BasePlugin> config = plugin.configure(
                new TranslationConfig<>(plugin, new File(folder, lang.getLanguageString() + ".yml")));
        config.save();
        loadedConfigs.put(lang, config);
    }

    @Override
    public String tr(Language lang, String path, Object... args) {

        return tr(lang, path, null, args);
    }

    @Override
    public String tr(Language lang, String path, String def, Object... args) {

        if (def == null) {
            def = "No translation for language " + lang + " and path: \"" + path + "\"";
        }

        if (!loadedConfigs.containsKey(lang)) {
            createConfig(lang);
        }
        TranslationConfig<?> config = loadedConfigs.get(lang);
        if (!config.isSet(path)) {
            config.set(path, def);
            config.save();
        }
        return String.format(config.getString(path), args);
    }

    @Override
    public String tr(CommandSender sender, String path, Object... args) {

        if (sender instanceof Player) {
            return tr((Player) sender, path, args);
        }
        return tr(Language.DEFAULT_LANGUAGE, path, args);
    }

    @Override
    public String tr(CommandSender sender, String path, String def, Object... args) {

        if (sender instanceof Player) {
            return tr((Player) sender, path, def, args);
        }
        return tr(Language.DEFAULT_LANGUAGE, path, def, args);
    }

    @Override
    public String tr(Player player, String path, Object... args) {

        return tr(Language.getLanguage(player), path, args);
    }

    @Override
    public String tr(Player player, String path, String def, Object... args) {

        return tr(Language.getLanguage(player), path, def, args);
    }

    @Override
    public String var(Language language, String path) {

        return tr(language, path);
    }

    @Override
    public String var(Language language, String path, String def) {

        return tr(language, path, def);
    }

    @Override
    public String var(CommandSender sender, String path) {

        return tr(sender, path);
    }

    @Override
    public String var(CommandSender sender, String path, String def) {

        return tr(sender, path, def);
    }

    @Override
    public String var(Player player, String path) {

        return tr(player, path);
    }

    @Override
    public String var(Player player, String path, String def) {

        return tr(player, path, def);
    }

    @Override
    public void msg(CommandSender sender, String path, Object... args) {

        if (sender instanceof Player) {
            msg((Player) sender, path, args);
            return;
        }
        sender.sendMessage(tr(sender, path, args));
    }

    @Override
    public void msg(CommandSender sender, String path, String def, Object... args) {

        if (sender instanceof Player) {
            msg((Player) sender, path, def, args);
            return;
        }
        sender.sendMessage(tr(sender, path, def, args));
    }

    @Override
    public void msg(Player player, String path, Object... args) {

        player.sendRawMessage(tr(player, path, args));
    }

    @Override
    public void msg(Player player, String path, String def, Object... args) {

        player.sendRawMessage(tr(player, path, def, args));
    }

    /**
     * Send a message with the requested String by path in the specified language of the Command Sender.
     *
     * @param sender The Command Sender
     * @param path   The path to the string in the config
     * @param color  The color to use for this message.
     * @param args   The Arguments referenced by the specifiers in the String.
     */
    @Override
    public void msg(final CommandSender sender, final String path, final ChatColor color, final Object... args) {

        sender.sendMessage(color + this.tr(sender, path, args));
    }

    /**
     * Send a message with the requested String by path in the specified language of the Command Sender.
     *
     * @param sender The Command Sender
     * @param path   The path to the string in the config
     * @param color  The color to use for this message.
     * @param def    The default value for the message if the path is not found or is not a String.
     * @param args   The Arguments referenced by the specifiers in the String.
     */
    @Override
    public void msg(final CommandSender sender, final String path, final ChatColor color, final String def, final Object... args) {

        sender.sendMessage(color + this.tr(sender, path, def, args));
    }

    /**
     * Send a message with the requested String by path in the specified language of the Player.
     *
     * @param player The Player
     * @param path   The path to the string in the config
     * @param color  The color to use for this message.
     * @param args   The Arguments referenced by the specifiers in the String.
     */
    @Override
    public void msg(final Player player, final String path, final ChatColor color, final Object... args) {

        player.sendRawMessage(color + tr(player, path, args));
    }

    /**
     * Send a message with the requested String by path in the specified language of the Player.
     *
     * @param player The Player
     * @param path   The path to the string in the config
     * @param color  The color to use for this message.
     * @param def    The default value for the message if the path is not found or is not a String.
     * @param args   The Arguments referenced by the specifiers in the String.
     */
    @Override
    public void msg(final Player player, final String path, final ChatColor color, final String def, final Object... args) {

        player.sendRawMessage(color + tr(player, path, def, args));
    }

    /**
     * Broadcast a message to all players in their specified language.
     *
     * @param path The path to the string in the config
     * @param args The Arguments referenced by the specifiers in the String.
     *
     * @return The number of player
     */
    @Override
    public int broadcastMessage(final String path, final Object... args) {

        return broadcast(path, null, null, args);
    }

    /**
     * Broadcast a message to all players in their specified language.
     *
     * @param path The path to the string in the config
     * @param def  The default value for the message if the path is not found or is not a String.
     * @param args The Arguments referenced by the specifiers in the String.
     *
     * @return The number of player
     */
    @Override
    public int broadcastMessage(final String path, final String def, final Object... args) {

        return broadcast(path, null, def, args);
    }

    /**
     * Broadcast a message to all players in their specified language.
     *
     * @param path  The path to the string in the config
     * @param color The color to use for this message.
     * @param def   The default value for the message if the path is not found or is not a String.
     * @param args  The Arguments referenced by the specifiers in the String.
     *
     * @return The number of player
     */
    @Override
    public int broadcastMessage(final String path, final ChatColor color, final String def, final Object... args) {

        return broadcast(path, color, def, args);
    }

    /**
     * Broadcast a message to all players in their specified language.
     *
     * @param path  The path to the string in the config
     * @param color The color to use for this message.
     * @param args  The Arguments referenced by the specifiers in the String.
     *
     * @return The number of player
     */
    @Override
    public int broadcastMessage(final String path, final ChatColor color, final Object... args) {

        return broadcast(path, color, null, args);
    }

    private int broadcast(final String path, final ChatColor color, final String def, final Object... args) {

        int count = 0;
        final String permission = "bukkit.broadcast.user";
        final Set<Permissible> permissibles = this.plugin.getServer().getPluginManager().getPermissionSubscriptions(permission);

        for (final Permissible permissible : permissibles) {
            if ((permissible instanceof Player) && permissible.hasPermission(permission)) {
                final Player player = (Player) permissible;
                if (color == null) {
                    this.msg(player, path, def, args);
                } else {
                    this.msg(player, path, color, def, args);
                }
                count++;
            }
        }

        return count;
    }
}
