package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.ConfigLoader;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.KeyValueMap;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.config.typeconversions.*;
import de.raidcraft.api.quests.Quests;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigUtil {

    private final static Pattern VARIABLE_PATTERN = Pattern.compile(".*#([\\w\\d\\s]+):([\\w\\d\\s]+)#.*");
    private final static Pattern THIS_PATH_PATTERN = Pattern.compile("^(?<prefix>[\\w@!\\-+?:_\\d\\.\\s\"\\[\\]]* ?)(?<path>((this\\.)|(\\.\\.\\/)+)[\\w\\d\\-_\\.\\/]+)(?<suffix> ?.*)$");
    private static final List<TypeConversion> typeConversions = new ArrayList<>(
            Arrays.asList(new SameTypeConversion(),
                    new StringTypeConversion(),
                    new BooleanTypeConversion(),
                    new NumberTypeConversion(),
                    new EnumTypeConversion(),
                    /*new ConfigurationBaseTypeConversion(),*/
                    new SetTypeConversion(),
                    new ListTypeConversion(),
                    new MapTypeConversion()
            )
    );

    public static String replaceCount(String path, String value, int count, int maxCount) {

        value = replacePathReference(value, path);
        value = value.replace("%current%", String.valueOf(count)).replace("%count%", String.valueOf(maxCount));
        return value;
    }

    public static <T extends ConfigurationSection> T replacePathReferences(T section, String basePath) {

        if (basePath.startsWith(".")) {
            basePath = basePath.replaceFirst("\\.", "");
        }
        final String path = basePath;

        final Function<String, String> replacer = (input) -> {

            input = input.replaceAll("(?<!\\.\\.)/", ".");
            Matcher thisMatcher;
            while ((thisMatcher = THIS_PATH_PATTERN.matcher(input)).matches()) {
                input = thisMatcher.group("prefix") + thisMatcher.replaceFirst(replacePathReference(thisMatcher.group("path"), path)) + thisMatcher.group("suffix");
            }
            return replaceVariableRefrences(path, input);
        };

        for (String key : section.getKeys(true)) {
            if (section.isString(key)) {
                section.set(key, replacer.apply(section.getString(key)));
            } else if (section.isList(key)) {
                List<String> stringList = section.getStringList(key);
                List<String> newList = new ArrayList<>();
                for (String item : stringList) {
                    newList.add(replacer.apply(item));
                }
                section.set(key, newList);
            }
        }
        return section;
    }

    public static String replacePathReference(String value, String basePath) {

        value = value.replaceAll("(?<!\\.\\.)/", ".");
        basePath = basePath.replaceAll("/", ".");
        if (value.startsWith("this.")) {
            value = value.replaceFirst("this", basePath).replaceFirst("^\\.", "");
        } else if (value.startsWith("../")) {
            String[] sections = basePath.split("\\.");
            basePath = "";
            for (int i = sections.length - 1; i >= 0; --i) {
                if (value.startsWith("../")) {
                    value = value.replaceFirst("^\\.\\./", "");
                } else {
                    basePath = sections[i] + "." + basePath.replaceFirst("^\\.", "");
                }
            }
            value = basePath.replaceFirst("\\.$", "") + "." + value.replaceFirst("^\\.", "");
        }
        return value.replaceFirst("^\\.", "");
    }

    public static String replaceVariableRefrences(String basePath, String value) {

        if (value == null || value.equals("")) {
            return value;
        }
        Matcher matcher = VARIABLE_PATTERN.matcher(value);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String name = replacePathReference(matcher.group(2), basePath);
            ConfigLoader loader = Quests.getQuestConfigLoader(type);
            if (loader != null) {
                try {
                    return loader.replaceReference(name);
                } catch (UnsupportedOperationException e) {
                    RaidCraft.LOGGER.warning("The Quest Config loader " + loader.getSuffix() + " does not support reference replacements!");
                }
            }
        }
        return value.replaceFirst("^\\.", "");
    }

    public static String getFileName(ConfigurationSection config) {

        if (config == null) return "NO-FILE";

        return getConfigurationBase(config)
                .map(configurationBase -> configurationBase.getFile().getAbsolutePath())
                .orElseGet(() -> config.getRoot() != null ? config.getRoot().getName() : "UNKNOWN-ROOT");
    }

    public static Optional<ConfigurationBase> getConfigurationBase(ConfigurationSection config) {
        if (config == null) return Optional.empty();
        ConfigurationBase base = null;
        if (config instanceof ConfigurationBase) {
            base = (ConfigurationBase) config;
        } else {
            Configuration root = config.getRoot();
            if (root == null) return Optional.empty();
            if (root instanceof ConfigurationBase) {
                base = (ConfigurationBase) root;
            }
        }
        return Optional.ofNullable(base);
    }

    public static void saveConfig(ConfigurationSection config) {

        getConfigurationBase(config).ifPresent(ConfigurationBase::save);
    }

    public static Object smartCast(Type genericType, Object value) {

        if (value == null) {
            return null;
        }
        Type[] neededGenerics;
        Class target = null;
        if (genericType != null && genericType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) genericType;
            Type raw = type.getRawType();
            if (raw instanceof Class) {
                target = (Class) raw;
            }
            neededGenerics = type.getActualTypeArguments();
        } else {
            if (genericType instanceof Class) {
                target = (Class) genericType;
            }
            neededGenerics = new Type[0];
        }

        if (target == null) {
            return null;
        }

        Object ret = null;

        for (TypeConversion conversion : typeConversions) {
            if ((ret = conversion.handle(target, neededGenerics, value)) != null) {
                break;
            }
        }

        return ret;
    }

    public static void registerTypeConversion(TypeConversion conversion) {

        typeConversions.add(conversion);
    }

    @SuppressWarnings("unchecked")
    public static Object prepareSerialization(Object obj) {

        if (obj instanceof Collection) {
            obj = new ArrayList((Collection) obj);
        }
        return obj;
    }

    public static ConfigurationSection parseKeyValueTable(List<KeyValueMap> map) {

        ConfigurationSection configuration = new MemoryConfiguration();
        for (KeyValueMap entry : map) {
            try {
                configuration.set(entry.getDataKey(), Double.parseDouble(entry.getDataValue()));
            } catch (NumberFormatException e) {
                Boolean value = BooleanUtils.toBooleanObject(entry.getDataValue());
                if (value == null) {
                    configuration.set(entry.getDataKey(), entry.getDataValue());
                } else {
                    configuration.set(entry.getDataKey(), value);
                }
            }
        }
        return configuration;
    }

    public static Location getLocationFromConfig(ConfigurationSection config, Player player) {

        if (config == null) return null;
        World world = LocationUtil.getCaseInsensitiveWorld(config.getString("world"));
        if (world == null) return null;

        return new Location(world,
                config.getInt("x"),
                config.getInt("y"),
                config.getInt("z"),
                (float) config.getDouble("yaw"),
                (float) config.getDouble("float"));
    }

    public static Location getLocationFromConfig(ConfigurationSection section) {

        return getLocationFromConfig(section, null);
    }

    public static void loadRecursiveConfigs(JavaPlugin plugin, String path, ConfigLoader... loaders) {

        loadRecursiveConfigs(plugin, path, Arrays.asList(loaders));
    }

    public static void loadRecursiveConfigs(JavaPlugin plugin, String path, Collection<ConfigLoader> loaders) {
        File dir = new File(plugin.getDataFolder(), path);
        dir.mkdirs();
        loadConfigs(dir, "", loaders);
    }

    private static void loadConfigs(File baseFolder, String path, Collection<ConfigLoader> loaders) {

        for (File file : Objects.requireNonNull(baseFolder.listFiles())) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                loadConfigs(file, path + "." + fileName.toLowerCase(), loaders);
            } else {
                path = StringUtils.strip(path, ".");
                for (ConfigLoader loader : loaders) {
                    if (!loader.matches(file)) continue;
                    loader.setPath(path);
                    String id = path + "." + file.getName().toLowerCase()
                            .replace(loader.getSuffix(), "");
                    id = StringUtils.strip(id,".");

                    SimpleConfiguration configFile = loader.getPlugin().configure(new SimpleConfiguration<>(loader.getPlugin(), file));
                    if (loader.isGenerateId() && !configFile.isSet("id")) {
                        configFile.set("id", UUID.randomUUID().toString());
                        configFile.save();
                    }

                    loader.loadConfig(id, configFile);
                }
            }
        }
    }
}

