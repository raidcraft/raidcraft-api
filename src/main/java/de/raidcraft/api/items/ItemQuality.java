package de.raidcraft.api.items;

import de.raidcraft.util.EnumUtils;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public enum ItemQuality {

    POOR("Schlecht", ChatColor.GRAY),
    COMMON("Verbreitet", ChatColor.WHITE),
    UNCOMMON("Selten", ChatColor.GREEN),
    RARE("Rar", ChatColor.BLUE),
    EPIC("Episch", ChatColor.DARK_PURPLE),
    LEGENDARY("Legendär", ChatColor.GOLD);

    private final String germanName;
    private final ChatColor color;

    private ItemQuality(String germanName, ChatColor color) {

        this.germanName = germanName;
        this.color = color;
    }

    public String getGermanName() {

        return germanName;
    }

    public ChatColor getColor() {

        return color;
    }

    public static ItemQuality fromGermanName(String name) {

        name = name.toLowerCase();
        for (ItemQuality item : values()) {
            if (item.getGermanName().toLowerCase().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static ItemQuality fromString(String str) {

        return EnumUtils.getEnumFromString(ItemQuality.class, str);
    }
}
