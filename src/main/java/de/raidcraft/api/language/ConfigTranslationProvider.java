package de.raidcraft.api.language;

import de.raidcraft.api.BasePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
    public String tr(Language lang, String key, Object... args) {

        return tr(lang, key, "No translation for language " + lang + " and key: \"" + key + "\"", args);
    }

    @Override
    public String tr(Language lang, String key, String def, Object... args) {

        if (!loadedConfigs.containsKey(lang)) {
            createConfig(lang);
        }
        TranslationConfig<?> config = loadedConfigs.get(lang);
        if (!config.isSet(key)) {
            config.set(key, def);
            config.save();
        }
        return String.format(config.getString(key), args);
    }

    @Override
    public String tr(CommandSender sender, String key, Object... args) {

        if (sender instanceof Player) {
            return tr((Player) sender, key, args);
        }
        return tr(Language.DEFAULT_LANGUAGE, key, args);
    }

    @Override
    public String tr(CommandSender sender, String key, String def, Object... args) {

        if (sender instanceof Player) {
            return tr((Player) sender, key, def, args);
        }
        return tr(Language.DEFAULT_LANGUAGE, key, def, args);
    }

    @Override
    public String tr(Player player, String key, Object... args) {

        return tr(Language.getLanguage(player), key, args);
    }

    @Override
    public String tr(Player player, String key, String def, Object... args) {

        return tr(Language.getLanguage(player), key, def, args);
    }

    @Override
    public String var(Language language, String key) {

        return tr(language, key);
    }

    @Override
    public String var(Language language, String key, String def) {

        return tr(language, key, def);
    }

    @Override
    public String var(CommandSender sender, String key) {

        return tr(sender, key);
    }

    @Override
    public String var(CommandSender sender, String key, String def) {

        return tr(sender, key, def);
    }

    @Override
    public String var(Player player, String key) {

        return tr(player, key);
    }

    @Override
    public String var(Player player, String key, String def) {

        return tr(player, key, def);
    }

    @Override
    public void msg(CommandSender sender, String key, Object... args) {

        if (sender instanceof Player) {
            msg((Player) sender, key, args);
            return;
        }
        sender.sendMessage(tr(sender, key, args));
    }

    @Override
    public void msg(CommandSender sender, String key, String def, Object... args) {

        if (sender instanceof Player) {
            msg((Player) sender, key, def, args);
            return;
        }
        sender.sendMessage(tr(sender, key, def, args));
    }

    @Override
    public void msg(Player player, String key, Object... args) {

        player.sendRawMessage(tr(player, key, args));
    }

    @Override
    public void msg(Player player, String key, String def, Object... args) {

        player.sendRawMessage(tr(player, key, def, args));
    }
}
