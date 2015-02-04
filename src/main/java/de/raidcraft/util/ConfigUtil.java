package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;
import de.raidcraft.api.mobs.Mobs;
import org.bukkit.configuration.ConfigurationSection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigUtil {

    private final static Pattern pattern = Pattern.compile(".*#([\\w\\d\\s]+):([\\w\\d\\s]+)#.*");

    public static ConfigurationSection replaceThisReferences(ConfigurationSection section, String basePath) {

        if (basePath.startsWith(".")) {
            basePath = basePath.replaceFirst("\\.", "");
        }
        for (String key : section.getKeys(true)) {
            if (section.isString(key)) {
                String value = section.getString(key);
                if (value.startsWith("this")) {
                    value = value.replaceFirst("this", basePath);
                }
                value = replaceRefrences(basePath, value);
                section.set(key, value);
            }
        }
        return section;
    }

    public static String replaceCount(String path, String value, int count, int maxCount) {

        value = replaceRefrences(path, value);
        value = value.replace("%current%", String.valueOf(count)).replace("%count%", String.valueOf(maxCount));
        return value;
    }

    public static String replaceRefrences(String basePath, String value) {

        if (value == null || value.equals("")) {
            return value;
        }
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String name = matcher.group(2);
            if (matcher.group(2).contains("this")) {
                if (basePath.startsWith(".")) {
                    basePath = basePath.replaceFirst("\\.", "");
                }
                name = name.replace("this", basePath);
            }
            if (type.equalsIgnoreCase("mob")) {
                return Mobs.getFriendlyName(name);
            } else if (type.equalsIgnoreCase("host")) {
                RaidCraft.getComponent(RaidCraftPlugin.class).warning("replaceRefrences: no host found for: " + basePath + ":" + value);
            }

            return name;
        }
        return value;
    }
}

