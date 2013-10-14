package de.raidcraft.api.quests.util;

import de.raidcraft.api.mobs.Mobs;
import de.raidcraft.api.quests.InvalidQuestHostException;
import de.raidcraft.api.quests.Quests;
import org.bukkit.configuration.ConfigurationSection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public class QuestUtil {

    private final static Pattern pattern = Pattern.compile(".*#([\\w\\d\\s]+):([\\w\\d\\s]+)#.*");

    public static ConfigurationSection replaceThisReferences(ConfigurationSection section, String basePath) {

        if (basePath.startsWith(".")) {
            basePath = basePath.replaceFirst("\\.", "");
        }
        for (String key : section.getKeys(true)) {
            if (section.getString(key).startsWith("this")) {
                section.set(key, section.getString(key).replaceFirst("this", basePath));
            }
        }
        return section;
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
                try {
                    return Quests.getQuestHost(name).getFriendlyName();
                } catch (InvalidQuestHostException ignored) {
                }
            }
            return name;
        }
        return value;
    }
}
