package de.raidcraft.api.items;

import com.avaje.ebean.annotation.EnumValue;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.ItemID;
import de.raidcraft.util.EnumUtils;

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

    public static EquipmentSlot fromItemId(int id) {

        switch (id) {

            case ItemID.CHAINMAIL_BOOTS:
            case ItemID.DIAMOND_BOOTS:
            case ItemID.IRON_BOOTS:
            case ItemID.GOLD_BOOTS:
            case ItemID.LEATHER_BOOTS:
                return FEET;
            case ItemID.HEAD:
            case ItemID.CHAINMAIL_HELMET:
            case ItemID.DIAMOND_HELMET:
            case ItemID.GOLD_HELMET:
            case ItemID.IRON_HELMET:
            case ItemID.LEATHER_HELMET:
                return HEAD;
            case ItemID.CHAINMAIL_CHEST:
            case ItemID.DIAMOND_CHEST:
            case ItemID.GOLD_CHEST:
            case ItemID.IRON_CHEST:
            case ItemID.LEATHER_CHEST:
                return CHEST;
            case ItemID.CHAINMAIL_PANTS:
            case ItemID.DIAMOND_PANTS:
            case ItemID.GOLD_PANTS:
            case ItemID.IRON_PANTS:
            case ItemID.LEATHER_PANTS:
                return LEGS;
            case BlockID.PISTON_BASE:
            case BlockID.PISTON_STICKY_BASE:
            case ItemID.IRON_DOOR_ITEM:
            case ItemID.WOODEN_DOOR_ITEM:
                return SHIELD_HAND;
            default:
                return null;
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
