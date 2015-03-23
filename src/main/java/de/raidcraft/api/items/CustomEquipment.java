package de.raidcraft.api.items;

/**
 * @author Silthus
 */
public interface CustomEquipment extends CustomItem, AttributeHolder {

    public EquipmentSlot getEquipmentSlot();

    public int getMaxDurability();
}
