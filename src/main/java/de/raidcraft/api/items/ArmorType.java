package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum ArmorType {

    @EnumValue("CLOTH")
    CLOTH("Stoff"),
    @EnumValue("LEATHER")
    LEATHER("Leder"),
    @EnumValue("MAIL")
    MAIL("Kette"),
    @EnumValue("PLATE")
    PLATE("Platte"),
    @EnumValue("SHIELD")
    SHIELD("Schild");

    private final String germanName;

    private ArmorType(String germanName) {

        this.germanName = germanName;
    }

    public String getGermanName() {

        return germanName;
    }

    public EquipmentSlot getEquipmentSlot(int itemId) {

        return EquipmentSlot.fromItemId(itemId);
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
