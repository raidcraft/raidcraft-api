package de.raidcraft.api.items;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
* @author mdoering
*/
public enum GemColor {

    ORANGE(ChatColor.GOLD),
    PURPLE(ChatColor.DARK_PURPLE),
    GREEN(ChatColor.GREEN),
    YELLOW(ChatColor.YELLOW),
    RED(ChatColor.RED),
    BLUE(ChatColor.BLUE),
    PRISMA(ChatColor.GRAY);

    @Getter
    private final ChatColor color;

    private GemColor(ChatColor color) {

        this.color = color;
    }

    public static GemColor fromChatColor(ChatColor color) {

        for (GemColor gemColor : values()) {
            if (gemColor.color.equals(color)) {
                return gemColor;
            }
        }
        return null;
    }

    public static GemColor fromString(String string) {

        for (GemColor color : values()) {
            if (color.name().equalsIgnoreCase(string)) {
                return color;
            }
        }
        string = string.replace(ChatColor.COLOR_CHAR + "", "");
        return fromChatColor(ChatColor.getByChar(string));
    }
}
