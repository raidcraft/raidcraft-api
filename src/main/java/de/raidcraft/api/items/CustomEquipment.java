package de.raidcraft.api.items;

import java.util.Set;

/**
 * @author Silthus
 */
public interface CustomEquipment extends CustomItem {

    public EquipmentSlot getEquipmentSlot();

    public Set<Attribute> getAttributes();

    public int getDurability();
}
