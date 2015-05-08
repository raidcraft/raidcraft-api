package de.raidcraft.api.items;

/**
 * @author mdoering
 */
public enum InventorySlot {

    MAIN_WEAPON_SLOT(0),
    OFFHAND_WEAPON_SLOT(1),
    HELMET(0),
    CHEST(1),
    LEGS(2),
    FEET(3),
    UNKNOWN(-1);

    private final int slot;

    InventorySlot(int slot) {

        this.slot = slot;
    }

    public static InventorySlot fromSlot(int slot) {

        for (InventorySlot inventorySlot : values()) {
            if (inventorySlot.slot == slot) {
                return inventorySlot;
            }
        }
        return UNKNOWN;
    }
}
