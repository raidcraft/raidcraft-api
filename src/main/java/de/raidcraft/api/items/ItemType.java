package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum ItemType {

    @EnumValue("WEAPON")
    WEAPON("Waffe"),
    @EnumValue("ARMOR")
    ARMOR("Rüstung");

    private final String germanName;

    private ItemType(String germanName) {

        this.germanName = germanName;
    }

    public String getGermanName() {

        return germanName;
    }

    public static ItemType fromGermanName(String name) {

        name = name.toLowerCase();
        for (ItemType type : values()) {
            if (type.getGermanName().toLowerCase().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static ItemType fromString(String str) {

        return EnumUtils.getEnumFromString(ItemType.class, str);
    }
}
