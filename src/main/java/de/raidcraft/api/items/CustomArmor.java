package de.raidcraft.api.items;

/**
 * @author Silthus
 */
public interface CustomArmor extends CustomEquipment {

    ArmorType getArmorType();

    void setArmorValue(int armorValue);

    int getArmorValue();
}
