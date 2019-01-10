package de.raidcraft.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.RaidCraftPlugin;

/**
 * @author Silthus
 */
public final class StringUtils {

    public static String formatName(String name) {

        return name == null ? "" : name.toLowerCase().replace(" ", "-").replace(".conv", "").trim();
    }

    public static double calculateAverageReadingTime(String text) {

        int wordsPerMinute = RaidCraft.getComponent(RaidCraftPlugin.class).getConfig().averageWordsPerMinute;
        double wordsPerSecond = wordsPerMinute / 60.0;
        String[] words = text.split("\\s");

        return Math.ceil(words.length / wordsPerSecond);
    }
}
