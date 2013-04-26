package de.raidcraft.api.items;

import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum WeaponType {

    SWORD("Schwert", EquipmentSlot.ONE_HANDED),
    AXE("Axt", EquipmentSlot.ONE_HANDED),
    POLEARM("Stangenwaffe", EquipmentSlot.TWO_HANDED),
    MACE("Streitkolben", EquipmentSlot.TWO_HANDED),
    STAFF("Stab", EquipmentSlot.TWO_HANDED),
    BOW("Bogen", EquipmentSlot.TWO_HANDED),
    MAGIC_WAND("Zauberstab", EquipmentSlot.ONE_HANDED);

    private final String germanName;
    private final EquipmentSlot equipmentSlot;

    private WeaponType(String germanName, EquipmentSlot equipmentSlot) {

        this.germanName = germanName;
        this.equipmentSlot = equipmentSlot;
    }

    public String getGermanName() {

        return germanName;
    }

    public EquipmentSlot getEquipmentSlot() {

        return equipmentSlot;
    }

    public static WeaponType fromGermanName(String name) {

        name = name.toLowerCase();
        for (WeaponType type : values()) {
            if (type.getGermanName().toLowerCase().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static WeaponType fromString(String name) {

        return EnumUtils.getEnumFromString(WeaponType.class, name);
    }
}
