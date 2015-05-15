package de.raidcraft.api.items;

/**
 * @author Silthus
 */
public interface CustomWeapon extends CustomEquipment {

    WeaponType getWeaponType();

    double getSwingTime();

    int getMinDamage();

    int getMaxDamage();
}
