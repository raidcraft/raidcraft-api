package de.raidcraft.api.items;

/**
 * @author Silthus
 */
public interface CustomArmor extends CustomEquipment {

    public ArmorType getArmorType();

    public void setArmorValue(int armorValue);

    public int getArmorValue();
}
