package de.raidcraft.api.items;

import de.raidcraft.util.EnumUtils;
import io.ebean.annotation.EnumValue;

/**
 * @author Silthus
 */
public enum WeaponType {

    @EnumValue("SWORD")
    SWORD("Schwert", EquipmentSlot.ONE_HANDED),
    @EnumValue("TWO_HAND_SWORD")
    TWO_HAND_SWORD("Zweihand Schwert", EquipmentSlot.TWO_HANDED),
    @EnumValue("DAGGER")
    DAGGER("Dolch", EquipmentSlot.ONE_HANDED),
    @EnumValue("AXE")
    AXE("Axt", EquipmentSlot.ONE_HANDED),
    @EnumValue("TWO_HAND_AXE")
    TWO_HAND_AXE("Zweihand Axt", EquipmentSlot.TWO_HANDED),
    @EnumValue("POLEARM")
    POLEARM("Stangenwaffe", EquipmentSlot.TWO_HANDED),
    @EnumValue("MACE")
    MACE("Streitkolben", EquipmentSlot.ONE_HANDED),
    @EnumValue("TWO_HAND_MACE")
    TWO_HAND_MACE("Zweihand Streitkolben", EquipmentSlot.TWO_HANDED),
    @EnumValue("STAFF")
    STAFF("Stab", EquipmentSlot.TWO_HANDED),
    @EnumValue("BOW")
    BOW("Bogen", EquipmentSlot.TWO_HANDED),
    @EnumValue("MAGIC_WAND")
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
