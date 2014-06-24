package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author mdoering
 */
@Data
public class ConfigBuilder<T extends BasePlugin> implements Listener {

    private static final Map<String, SectionBuilder> SECTION_BUILDERS = new CaseInsensitiveMap<>();
    private static final Map<UUID, ConfigBuilder> CURRENT_BUILDERS = new HashMap<>();

    public static void registerConfigBuilder(SectionBuilder builder) throws ConfigBuilderException {

        try {
            Method method = builder.getClass().getMethod("createSection", CommandContext.class, Player.class);
            if (method.isAnnotationPresent(SectionBuilder.Information.class)) {
                String name = method.getAnnotation(SectionBuilder.Information.class).value();
                if (SECTION_BUILDERS.containsKey(name)) {
                    throw new ConfigBuilderException("Config Builder with the same name is already registered: " + name);
                }
                SECTION_BUILDERS.put(name, builder);
            } else {
                throw new ConfigBuilderException("Config Builder " + builder.getClass().getCanonicalName() + " has no Information Annotaion!");
            }
        } catch (NoSuchMethodException e) {
            throw new ConfigBuilderException(e);
        }
    }

    public static void registerConfigBuilder(Object builder) {

        if (builder instanceof SectionBuilder) {
            try {
                registerConfigBuilder((SectionBuilder) builder);
            } catch (ConfigBuilderException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
    }

    public static Map<String, SectionBuilder> getSectionBuilders() {

        return new HashMap<>(SECTION_BUILDERS);
    }

    public static SectionBuilder getSectionBuilder(String identifier) {

        return SECTION_BUILDERS.get(identifier);
    }

    public static boolean hasSectionBuilder(String identifier) {

        return SECTION_BUILDERS.containsKey(identifier);
    }

    public static boolean isBuilder(Player player) {

        return CURRENT_BUILDERS.containsKey(player.getUniqueId());
    }

    public static ConfigBuilder getBuilder(Player player) {

        return CURRENT_BUILDERS.getOrDefault(player.getUniqueId(),
                new ConfigBuilder<>(RaidCraft.getComponent(RaidCraftPlugin.class), player, "config-builders"));
    }

    /* Start the actual builder class here */

    private final T plugin;
    private final Player player;
    private final String baseFilePath;
    private final File basePath;
    private final List<ConfigurationBase<T>> configs = new ArrayList<>();
    private ConfigurationBase<T> currentConfig;
    private String currentPath;
    private boolean multiSection = false;
    private int multiSectionCount = 0;

    protected ConfigBuilder(T plugin, Player player, String baseFilePath) {

        this.plugin = plugin;
        this.player = player;
        this.baseFilePath = baseFilePath;
        this.basePath = new File(plugin.getDataFolder(), baseFilePath);
        plugin.registerEvents(this);
        CURRENT_BUILDERS.put(player.getUniqueId(), this);
    }

    public ConfigurationBase<T> getCurrentConfig() {

        if (currentConfig == null) {
            createConfig("default.yml");
        }
        return currentConfig;
    }

    public ConfigurationSection getCurrentSection() {

        return getCurrentConfig().getOverrideSection(getCurrentPath());
    }

    public void setCurrentPath(String path) {

        this.currentPath = path;
        setMultiSectionCount(0);
    }

    public ConfigurationBase<T> createConfig(String name) {

        ConfigurationBase<T> oldConfig = currentConfig;
        if (currentConfig != null) {
            configs.add(currentConfig);
        }
        currentConfig = new SimpleConfiguration<>(getPlugin(), new File(getBasePath(), name));
        return oldConfig;
    }

    public void appendSection(ConfigurationSection section) {

        if (isMultiSection()) {
            multiSectionCount++;
            getCurrentSection().set(multiSectionCount + "", section);
        } else {
            getCurrentConfig().set(getCurrentPath(), section);
        }
    }

    public void appendSection(String path, ConfigurationSection section) {

        setCurrentPath(path);
        appendSection(section);
    }

    public void save() {

        configs.forEach(ConfigurationBase::save);
        CURRENT_BUILDERS.remove(getPlayer().getUniqueId());
        HandlerList.unregisterAll(this);
    }
}
