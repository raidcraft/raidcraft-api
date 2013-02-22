package de.raidcraft.util;

/**
 * @author Silthus
 */
public final class StringUtils {

    public static String formatName(String name) {

        return name.toLowerCase().replace(" ", "-").trim();
    }
}
