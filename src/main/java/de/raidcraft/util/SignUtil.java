package de.raidcraft.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Philip
 * Date: 17.09.12 - 21:48
 * Description:
 */
public final class SignUtil {

    private SignUtil() {

    }

    /**
     * Checks if the given line on the sign is equals to
     * the given text colored in the given color.
     *
     * @param strSign colored sign text
     * @param strText text to compare
     *
     * @return true if text is equal
     */
    public static boolean isLineEqual(String strSign, String strText) {

        strSign = strip(strSign);
        strText = strip(strText);
        return strText.equalsIgnoreCase(strSign);
    }

    public static String strip(String strText) {

        strText = ChatColor.stripColor(strText).trim();
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(strText);
        if (matcher.matches()) {
            strText = matcher.group(1);
        }
        return strText;
    }

    public static boolean isSign(Block block) {

        Material type = block.getType();
        return type == Material.SIGN || type == Material.WALL_SIGN || type == Material.SIGN_POST;
    }

    public static String parseColor(String line) {

        String regex = "&(?<!&&)(?=%c)";
        Formatter fmt;
        for (ChatColor clr : ChatColor.values()) {
            char code = clr.getChar();
            fmt = new Formatter();
            line = line.replaceAll(fmt.format(regex, code).toString(), "\u00A7");
        }
        return line.replace("&&", "&");
    }
}
