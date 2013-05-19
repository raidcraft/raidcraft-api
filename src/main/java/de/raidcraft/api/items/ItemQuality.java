package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import de.raidcraft.util.EnumUtils;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public enum ItemQuality {

    @EnumValue("POOR")
    POOR("Schlecht", ChatColor.GRAY),
    @EnumValue("COMMON")
    COMMON("Verbreitet", ChatColor.WHITE),
    @EnumValue("UNCOMMON")
    UNCOMMON("Selten", ChatColor.GREEN),
    @EnumValue("RARE")
    RARE("Rar", ChatColor.BLUE),
    @EnumValue("EPIC")
    EPIC("Episch", ChatColor.DARK_PURPLE),
    @EnumValue("LEGENDARY")
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
