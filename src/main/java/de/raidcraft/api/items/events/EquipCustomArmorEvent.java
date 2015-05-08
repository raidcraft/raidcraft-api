package de.raidcraft.api.items.events;

import de.raidcraft.api.items.ArmorType;
import de.raidcraft.api.items.CustomArmor;
import de.raidcraft.api.items.InventorySlot;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
public class EquipCustomArmorEvent extends EquipCustomItemEvent {

    private final CustomArmor armor;
    private final ArmorType armorType;

    public EquipCustomArmorEvent(Player player, CustomArmor customArmor, InventorySlot equipmentSlot) {

        super(player, customArmor, equipmentSlot);
        this.armor = customArmor;
        this.armorType = customArmor.getArmorType();
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
