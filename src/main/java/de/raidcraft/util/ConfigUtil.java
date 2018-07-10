package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.ConfigLoader;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.KeyValueMap;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.config.typeconversions.*;
import de.raidcraft.api.quests.Quests;
import org.apache.commons.lang.BooleanUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigUtil {

    private final static Pattern VARIABLE_PATTERN = Pattern.compile(".*#([\\w\\d\\s]+):([\\w\\d\\s]+)#.*");
    private final static Pattern THIS_PATH_PATTERN = Pattern.compile("(this\\.)");
    private final static Pattern PREVIOUS_FOLDER_PATTERN = Pattern.compile("(\\.\\./)");
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

    public static ConfigurationSection replacePathReferences(ConfigurationSection section, String basePath) {

        if (basePath.startsWith(".")) {
            basePath = basePath.replaceFirst("\\.", "");
        }
        String[] paths = basePath.split("\\.");
        String previousBasePath = "";
        for (int i = 0; i < paths.length - 1; i++) {
            previousBasePath += paths[i] + ".";
        }
        for (String key : section.getKeys(true)) {
            if (section.isString(key)) {
                String value = replacePathReference(section.getString(key), basePath);
                value = replaceRefrences(basePath, value);
                section.set(key, value);
            } else if (section.isList(key)) {
                List<String> stringList = section.getStringList(key);
                List<String> newList = new ArrayList<>();
                for (String item : stringList) {
                    item = THIS_PATH_PATTERN.matcher(item).replaceAll(basePath + ".");
                    item = PREVIOUS_FOLDER_PATTERN.matcher(item).replaceAll(previousBasePath);
                    item = replaceRefrences(basePath, item);
                    newList.add(item);
                }
                section.set(key, newList);
            }
        }
        return section;
    }

    public static String replacePathReference(String value, String basePath) {

        if (value.startsWith("this.")) {
            value = value.replaceFirst("this", basePath);
        } else if (value.startsWith("../")) {
            String[] sections = basePath.split("\\.");
            basePath = "";
            for (int i = sections.length; i >= 0; --i) {
                if (value.startsWith("../")) {
                    value = value.replace("\\.\\./", "");
                } else {
                    basePath = sections[i] + "." + basePath;
                }
            }
            value = basePath + value;
        }
        return value;
    }

    public static String replaceRefrences(String basePath, String value) {

        if (value == null || value.equals("")) {
            return value;
        }
        if (basePath.startsWith(".")) {
            basePath = basePath.replaceFirst("\\.", "");
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
        return value;
    }

    public static String getFileName(ConfigurationSection config) {

        ConfigurationBase base = null;
        if (config instanceof ConfigurationBase) {
            base = (ConfigurationBase) config;
        } else {
            Configuration root = config.getRoot();
            if (root != null && root instanceof ConfigurationBase) {
                base = (ConfigurationBase) root;
            }
        }
        if (base != null) {
            return base.getFile().getAbsolutePath();
        } else {
            return config.getRoot().getName();
        }
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

        World world = Bukkit.getWorld(config.getString("world"));
        if (world == null) {
            if (player != null) {
                world = player.getWorld();
            } else {
                Optional<World> any = Bukkit.getWorlds().stream().findAny();
                if (!any.isPresent()) return null;
                world = any.get();
            }
        }
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
                if (path.startsWith(".")) {
                    path = path.replaceFirst("\\.", "");
                }
                for (ConfigLoader loader : loaders) {
                    if (!loader.matches(file)) continue;
                    loader.setPath(path);
                    String id = (path + "." + file.getName().toLowerCase()).replace(loader.getSuffix(), "").replaceFirst("\\.", "");
                    ConfigurationSection configFile = loader.getPlugin().configure(new SimpleConfiguration<>(loader.getPlugin(), file));

                    loader.loadConfig(id, configFile);
                }
            }
        }
    }
}

