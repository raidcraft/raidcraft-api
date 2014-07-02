package de.raidcraft.api.config.builder;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nullable;
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

    private static final Map<String, ConfigGenerator> CONFIG_GENERATORS = new CaseInsensitiveMap<>();
    private static final Map<String, ConfigGenerator.Information> GENERATOR_INFORMATIONS = new CaseInsensitiveMap<>();
    private static final Map<UUID, ConfigBuilder> CURRENT_BUILDERS = new HashMap<>();

    public static void registerConfigGenerator(ConfigGenerator generator) {

        for (Method method : generator.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConfigGenerator.Information.class)) {
                if (method.getParameterTypes().length == 3
                        && method.getParameterTypes()[0] == ConfigBuilder.class
                        && method.getParameterTypes()[1] == CommandContext.class
                        && method.getParameterTypes()[2] == Player.class) {
                    ConfigGenerator.Information information = method.getAnnotation(ConfigGenerator.Information.class);
                    String name = information.value();
                    if (!CONFIG_GENERATORS.containsKey(name)) {
                        CONFIG_GENERATORS.put(name, generator);
                        GENERATOR_INFORMATIONS.put(name, information);
                        RaidCraft.LOGGER.warning("Config Builder with the same name is already registered: " + name);
                    }
                }
            }
        }
    }

    public static void registerConfigGenerator(Object builder) {

        if (builder instanceof ConfigGenerator) {
            registerConfigGenerator((ConfigGenerator) builder);
        }
    }

    @Nullable
    public static Method getConfigGeneratorMethod(ConfigGenerator generator, String name) {

        for (Method method : generator.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ConfigGenerator.Information.class)) {
                if (method.getParameterTypes().length == 3
                        && method.getParameterTypes()[0] == ConfigBuilder.class
                        && method.getParameterTypes()[1] == CommandContext.class
                        && method.getParameterTypes()[2] == Player.class) {
                    ConfigGenerator.Information information = method.getAnnotation(ConfigGenerator.Information.class);
                    if (information.value().equalsIgnoreCase(name)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    public static Map<String, ConfigGenerator> getConfigGenerators() {

        return new HashMap<>(CONFIG_GENERATORS);
    }

    public static ConfigGenerator getConfigGenerator(String identifier) {

        return CONFIG_GENERATORS.get(identifier);
    }

    public static boolean hasConfigGenerator(String identifier) {

        return CONFIG_GENERATORS.containsKey(identifier);
    }

    @Nullable
    public static ConfigGenerator.Information getConfigGeneratorInformation(String identifier) {

        return GENERATOR_INFORMATIONS.get(identifier);
    }

    public static boolean isBuilder(Player player) {

        return CURRENT_BUILDERS.containsKey(player.getUniqueId());
    }

    public static ConfigBuilder getBuilder(Player player) {

        return CURRENT_BUILDERS.getOrDefault(player.getUniqueId(),
                new ConfigBuilder<>(RaidCraft.getComponent(RaidCraftPlugin.class), player, "config-builders"));
    }

    public static <T extends BasePlugin> ConfigBuilder<T> createBuilder(T plugin, Player player, String baseFilePath) {

        ConfigBuilder<T> builder = new ConfigBuilder<>(plugin, player, baseFilePath);
        if (isBuilder(player)) {
            getBuilder(player).save();
        }
        CURRENT_BUILDERS.put(player.getUniqueId(), builder);
        return builder;
    }

    public static void checkArguments(CommandSender sender, CommandContext args, ConfigGenerator generator, String name) throws ConfigBuilderException {

        ConfigGenerator.Information information = generator.getInformation(name);
        if (args.argsLength() < information.min()) {
            generator.printHelp(sender, name);
            throw new ConfigBuilderException("Not enough arguments!");
        }
        if (args.argsLength() > information.max()) {
            generator.printHelp(sender, name);
            throw new ConfigBuilderException("Too many arguments!");
        }
        if (!information.anyFlags()) {
            char[] chars = information.flags().replace(":", "").toCharArray();
            for (char c : chars) {
                if (!args.getFlags().contains(c)) {
                    generator.printHelp(sender, name);
                    throw new ConfigBuilderException("Unknown flag: " + c);
                }
            }
        }
    }

    /* Start the actual builder class here */

    private final T plugin;
    private final Player player;
    private final String baseFilePath;
    private final File basePath;
    private final List<ConfigurationBase<T>> configs = new ArrayList<>();
    private final Map<String, Integer> multiSectionCount = new CaseInsensitiveMap<>();
    private ConfigurationBase<T> currentConfig;
    private String currentPath = "";
    private boolean locked = false;

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
        getMultiSectionCount().put(path, getMultiSectionCount(path));
    }

    public int getMultiSectionCount(String path) {

        return getMultiSectionCount().getOrDefault(path, 0);
    }

    /**
     * Creates a new config file, stores and returns the old one.
     * @param name to create
     * @return old config file
     */
    public ConfigurationBase<T> createConfig(String name) {

        ConfigurationBase<T> oldConfig = currentConfig;
        if (currentConfig != null) {
            configs.add(currentConfig);
        }
        currentConfig = new SimpleConfiguration<>(getPlugin(), new File(getBasePath(), name));
        multiSectionCount.clear();
        locked = false;
        return oldConfig;
    }

    /**
     * Creates new config file and populates it with the given content.
     * Stores and returns the old config.
     * @param name to create
     * @param content to store
     * @return old config file
     */
    public ConfigurationBase<T> createConfig(String name, ConfigurationSection content) {

        ConfigurationBase<T> oldConfig = createConfig(name);
        getCurrentConfig().merge(content);
        return oldConfig;
    }

    public void append(ConfigGenerator generator, ConfigurationSection section, String path, String name) throws ConfigBuilderException {

        if (isLocked()) {
            throw new ConfigBuilderException("The current config is finished. Please create a new one first: /rccb create <config_name>.yml");
        }
        if (!getCurrentPath().equals(path)) {
            setCurrentPath(path);
        }
        ConfigGenerator.Information information = generator.getInformation(name);
        if (information.multiSection()) {
            getMultiSectionCount().put(getCurrentPath(), getMultiSectionCount(path) + 1);
            getCurrentSection().set(getMultiSectionCount(getCurrentPath()) + "", section);
        } else {
            getCurrentConfig().set(getCurrentPath(), section);
        }
    }

    public void save() {

        configs.forEach(ConfigurationBase::save);
        CURRENT_BUILDERS.remove(getPlayer().getUniqueId());
        HandlerList.unregisterAll(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (isBuilder(event.getPlayer())) {
            getBuilder(event.getPlayer()).save();
        }
    }
}
