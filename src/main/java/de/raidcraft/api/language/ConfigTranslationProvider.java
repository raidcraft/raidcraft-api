package de.raidcraft.api.language;

import de.raidcraft.api.BasePlugin;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class ConfigTranslationProvider implements TranslationProvider {

    private final BasePlugin plugin;
    private final Map<Language, TranslationConfig<?>> loadedConfigs = new EnumMap<>(Language.class);

    public ConfigTranslationProvider(BasePlugin plugin) {

        this.plugin = plugin;
        File folder = new File(plugin.getDataFolder(), "languages/");
        folder.mkdirs();
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                Language language = Language.fromString(file.getName().replace(".yml", ""));
                if (language != null) {
                    loadedConfigs.put(language, plugin.configure(new TranslationConfig<>(plugin, file), false));
                }
            }
        }
    }

    @Override
    public String tr(Language lang, String key, Object... args) {

        return tr(lang, key, "No translation for language " + lang + " and key: \"" + key + "\"", args);
    }

    @Override
    public String tr(Language lang, String key, String def, Object... args) {

        if (loadedConfigs.containsKey(lang)) {
            return String.format(loadedConfigs.get(lang).getString(key),args);
        }
        return String.format(def, args);
    }

    @Override
    public String tr(Player player, String key, Object... args) {

        return tr(Language.getLanguage(player), key, args);
    }

    @Override
    public String tr(Player player, String key, String def, Object... args) {

        return tr(Language.getLanguage(player), key, def, args);
    }

}
