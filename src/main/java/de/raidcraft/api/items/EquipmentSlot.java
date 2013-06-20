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
    ONE_HANDED("Einhändig"),
    @EnumValue("SHIELD_HAND")
    SHIELD_HAND("Schildhand"),
    @EnumValue("TWO_HANDED")
    TWO_HANDED("Zweihändig"),
    @EnumValue("HEAD")
    HEAD("Kopf"),
    @EnumValue("CHEST")
    CHEST("Brust"),
    @EnumValue("LEGS")
    LEGS("Beine"),
    @EnumValue("FEET")
    FEET("Füße"),
    @EnumValue("HANDS")
    HANDS("Hände"),
    @EnumValue("INVENTORY")
    INVENTORY("Inventar");

    private final String germanName;

    private EquipmentSlot(String germanName) {

        this.germanName = germanName;
    }

    public String getGermanName() {

        return germanName;
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
