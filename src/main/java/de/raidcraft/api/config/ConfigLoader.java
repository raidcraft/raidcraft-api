package de.raidcraft.api.config;

import de.raidcraft.api.BasePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

/**
 * Handles the loading of the given file type.
 * The loader will be called for each file depending on the suffix match.
 */
@Getter
public abstract class ConfigLoader implements Comparable<ConfigLoader> {

    private final BasePlugin plugin;
    private final String suffix;
    private int priority = 1;
    @Setter
    private String path;

    public ConfigLoader(BasePlugin plugin) {
        this.plugin = plugin;
        this.suffix = ".yml";
    }

    public ConfigLoader(BasePlugin plugin, String suffix) {
        this.plugin = plugin;
        this.suffix = ("." + suffix + ".yml").toLowerCase();
    }

    public ConfigLoader(BasePlugin plugin, String suffix, int priority) {
        this(plugin, suffix);
        this.priority = priority;
    }

    /**
     * Implement your custom config loading in this function.
     * This will be called for every file that matches the given {@link #getSuffix()}.
     *
     * @param id     unique id of the config based on its path and file name without the suffix.
     *               Subfolders will be separated by a colon, e.g.
     *               subfolder1.subfolder2.my-config
     * @param config loaded config file not null
     */
    public abstract void loadConfig(String id, ConfigurationSection config);

    public String replaceReference(String key) {

        throw new UnsupportedOperationException();
    }

    /**
     * Tests if the loader suffix matches the suffix of the given file.
     *
     * @param file to match against
     * @return true if the loader matches and {@link #loadConfig(String, ConfigurationSection)} should be called.
     */
    public boolean matches(File file) {
        return file.getName().toLowerCase().endsWith(getSuffix());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof ConfigLoader)) return false;

        ConfigLoader loader = (ConfigLoader) o;

        return suffix.equals(loader.suffix);
    }

    @Override
    public int hashCode() {

        return suffix.hashCode();
    }

    @Override
    public int compareTo(ConfigLoader o) {

        return Integer.compare(getPriority(), o.getPriority());
    }
}
