package de.raidcraft.api.items;

import de.raidcraft.util.EnumUtils;
import io.ebean.annotation.EnumValue;
import org.bukkit.Material;

/**
 * @author Silthus
 */
public enum EquipmentSlot {

    @EnumValue("ONE_HANDED")
    ONE_HANDED("Einhändig", 0.4129, 0.0),
    @EnumValue("SHIELD_HAND")
    SHIELD_HAND("Schildhand", 0.4129, 0.0),
    @EnumValue("TWO_HANDED")
    TWO_HANDED("Zweihändig", 1.0, 0.0),
    @EnumValue("HEAD")
    HEAD("Kopf", 0.75, 0.8125),
    @EnumValue("CHEST")
    CHEST("Brust", 1.0, 1.0),
    @EnumValue("LEGS")
    LEGS("Beine", 1.0, 0.875),
    @EnumValue("FEET")
    FEET("Füße", 0.5625, 0.6875),
    @EnumValue("HANDS")
    HANDS("Hände", 0.4129, 0.0),
    @EnumValue("INVENTORY")
    INVENTORY("Inventar", 1.0, 0.0),
    @EnumValue("UNDEFINED")
    UNDEFINED("Undefiniert", 1.0, 0.0);

    private final String germanName;
    private final double slotModifier;
    private final double armorSlotModifier;

    private EquipmentSlot(String germanName, double slotModifier, double armorSlotModifier) {

        this.germanName = germanName;
        this.slotModifier = slotModifier;
        this.armorSlotModifier = armorSlotModifier;
    }

    public String getGermanName() {

        return germanName;
    }

    public double getSlotModifier() {

        return slotModifier;
    }

    public double getArmorSlotModifier() {

        return armorSlotModifier;
    }

    public static EquipmentSlot fromMaterial(Material material) {

        switch (material) {

            case CHAINMAIL_BOOTS:
            case DIAMOND_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case LEATHER_BOOTS:
                return FEET;
            case PLAYER_HEAD:
            case CHAINMAIL_HELMET:
            case DIAMOND_HELMET:
            case GOLDEN_HELMET:
            case IRON_HELMET:
            case LEATHER_HELMET:
                return HEAD;
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case IRON_CHESTPLATE:
            case LEATHER_CHESTPLATE:
                return CHEST;
            case CHAINMAIL_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case IRON_LEGGINGS:
            case LEATHER_LEGGINGS:
                return LEGS;
            case PISTON_HEAD:
            case STICKY_PISTON:
            case IRON_DOOR:
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case OAK_DOOR:
            case SPRUCE_DOOR:
                return SHIELD_HAND;
            default:
                return null;
        }
    }

    public static EquipmentSlot fromArmorSlotIndex(int index) {

        switch (index) {
            case 0:
                return FEET;
            case 1:
                return LEGS;
            case 2:
                return CHEST;
            case 3:
                return HEAD;
            default:
                return UNDEFINED;
        }
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
