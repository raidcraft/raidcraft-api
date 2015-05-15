package de.raidcraft.api.items;

/**
 * @author Silthus
 */
public interface CustomEquipment extends CustomItem, AttributeHolder {

    EquipmentSlot getEquipmentSlot();

    int getMaxDurability();
}
