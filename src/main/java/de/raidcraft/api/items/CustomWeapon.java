package de.raidcraft.api.items;

/**
 * @author Silthus
 */
public interface CustomWeapon extends CustomEquipment {

    public WeaponType getType();

    public double getSwingTime();

    public int getMinDamage();

    public int getMaxDamage();
}
