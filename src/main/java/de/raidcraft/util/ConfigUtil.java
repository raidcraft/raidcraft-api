package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.KeyValueMap;
import de.raidcraft.api.config.typeconversions.BooleanTypeConversion;
import de.raidcraft.api.config.typeconversions.EnumTypeConversion;
import de.raidcraft.api.config.typeconversions.ListTypeConversion;
import de.raidcraft.api.config.typeconversions.MapTypeConversion;
import de.raidcraft.api.config.typeconversions.NumberTypeConversion;
import de.raidcraft.api.config.typeconversions.SameTypeConversion;
import de.raidcraft.api.config.typeconversions.SetTypeConversion;
import de.raidcraft.api.config.typeconversions.StringTypeConversion;
import de.raidcraft.api.config.typeconversions.TypeConversion;
import de.raidcraft.api.quests.QuestConfigLoader;
import de.raidcraft.api.quests.Quests;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigUtil {

    private final static Pattern pattern = Pattern.compile(".*#([\\w\\d\\s]+):([\\w\\d\\s]+)#.*");
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
        for (String key : section.getKeys(true)) {
            if (section.isString(key)) {
                String value = replacePathReference(section.getString(key), basePath);
                value = replaceRefrences(basePath, value);
                section.set(key, value);
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
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String name = replacePathReference(matcher.group(2), basePath);
            QuestConfigLoader loader = Quests.getQuestConfigLoader(type);
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
                try {
                    configuration.set(entry.getDataKey(), Boolean.parseBoolean(entry.getDataValue()));
                } catch (NumberFormatException e1) {
                    configuration.set(entry.getDataKey(), entry.getDataValue());
                }
            }
        }
        return configuration;
    }

    public static Location getLocationFromConfig(ConfigurationSection config, Player player) {

        World world = Bukkit.getWorld(config.getString("world"));
        if (world == null && player != null && !config.isSet("world")) {
            world = player.getWorld();
        } else {
            Optional<World> any = Bukkit.getWorlds().stream().findAny();
            if (!any.isPresent()) return null;
            world = any.get();
        }
        return new Location(world, config.getInt("x"), config.getInt("y"), config.getInt("z"));
    }

    public static Location getLocationFromConfig(ConfigurationSection section) {

        return getLocationFromConfig(section, null);
    }
}

