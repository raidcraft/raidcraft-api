package de.raidcraft.api.items.events;

import de.raidcraft.api.items.CustomWeapon;
import de.raidcraft.api.items.InventorySlot;
import de.raidcraft.api.items.WeaponType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
public class EquipCustomWeaponEvent extends EquipCustomItemEvent {

    private final CustomWeapon weapon;
    private final WeaponType weaponType;

    public EquipCustomWeaponEvent(Player player, CustomWeapon customWeapon, InventorySlot equipmentSlot) {

        super(player, customWeapon, equipmentSlot);
        this.weapon = customWeapon;
        this.weaponType = customWeapon.getWeaponType();
    }

    //<-- Handler -->//
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
