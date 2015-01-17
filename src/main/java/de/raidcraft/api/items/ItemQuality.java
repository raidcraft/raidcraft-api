package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import de.raidcraft.util.EnumUtils;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public enum ItemQuality {

    @EnumValue("POOR")
    POOR("Schlecht", ChatColor.GRAY, 0, 0),
    @EnumValue("COMMON")
    COMMON("Verbreitet", ChatColor.WHITE, 0, 0),
    @EnumValue("UNCOMMON")
    UNCOMMON("Selten", ChatColor.GREEN, 2.0, 8.0),
    @EnumValue("RARE")
    RARE("Rar", ChatColor.BLUE, 1.8, 0.75),
    @EnumValue("EPIC")
    EPIC("Episch", ChatColor.DARK_PURPLE, 1.2, 26),
    @EnumValue("LEGENDARY")
    LEGENDARY("Legendär", ChatColor.GOLD, 1.0, 50);

    private final String germanName;
    private final ChatColor color;
    private final double qualityMultiplier;
    private final double qualityModifier;

    private ItemQuality(String germanName, ChatColor color, double qualityMultiplier, double qualityModifier) {

        this.germanName = germanName;
        this.color = color;
        this.qualityMultiplier = qualityMultiplier;
        this.qualityModifier = qualityModifier;
    }

    public String getGermanName() {

        return germanName;
    }

    public ChatColor getColor() {

        return color;
    }

    public double getQualityMultiplier() {

        return qualityMultiplier;
    }

    public double getQualityModifier() {

        return qualityModifier;
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
