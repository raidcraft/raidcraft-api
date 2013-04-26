package de.raidcraft.api.items;

/**
 * @author Silthus
 */
public interface CustomWeapon extends CustomEquipment {

    public WeaponType getWeaponType();

    public double getSwingTime();

    public int getMinDamage();

    public int getMaxDamage();
}
