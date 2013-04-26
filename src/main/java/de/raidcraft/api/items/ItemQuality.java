package de.raidcraft.api.items;

import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum ItemQuality {

    POOR("Schlecht"),
    COMMON("Verbreitet"),
    UNCOMMON("Selten"),
    RARE("Rar"),
    EPIC("Episch"),
    LEGENDARY("Legendär");

    private final String germanName;

    private ItemQuality(String germanName) {

        this.germanName = germanName;
    }

    public String getGermanName() {

        return germanName;
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
