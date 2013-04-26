package de.raidcraft.api.items;

import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum EquipmentSlot {

    ONE_HANDED("Einhändig"),
    SHIELD_HAND("Schildhand"),
    TWO_HANDED("Zweihändig"),
    HEAD("Kopf"),
    CHEST("Brust"),
    LEGS("Beine"),
    FEET("Füße");

    private final String germanName;

    private EquipmentSlot(String germanName) {

        this.germanName = germanName;
    }

    public String getGermanName() {

        return germanName;
    }

    public static EquipmentSlot fromGermanName(String name) {

        name = name.toLowerCase();
        for (EquipmentSlot slot : values()) {
            if (slot.getGermanName().toLowerCase().equals(name)) {
                return slot;
            }
        }
        return null;
    }

    public static EquipmentSlot fromString(String str) {

        return EnumUtils.getEnumFromString(EquipmentSlot.class, str);
    }
}
