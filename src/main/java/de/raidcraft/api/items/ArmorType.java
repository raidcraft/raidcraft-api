package de.raidcraft.api.items;

import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum ArmorType {

    CLOTH("Stoff"),
    LEATHER("Leder"),
    MAIL("Kette"),
    PLATE("Platte");

    private final String germanName;

    private ArmorType(String germanName) {

        this.germanName = germanName;
    }

    public String getGermanName() {

        return germanName;
    }

    public static ArmorType fromGermanName(String name) {

        name = name.toLowerCase();
        for (ArmorType type : values()) {
            if (type.getGermanName().toLowerCase().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static ArmorType fromString(String str) {

        return EnumUtils.getEnumFromString(ArmorType.class, str);
    }
}
