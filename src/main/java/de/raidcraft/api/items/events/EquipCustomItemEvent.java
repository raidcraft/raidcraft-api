package de.raidcraft.api.items.events;

import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.InventorySlot;
import de.raidcraft.api.items.ItemType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author mdoering
 */
@Getter
@Setter
public class EquipCustomItemEvent extends Event implements Cancellable {

    private final Player player;
    private final CustomItem customItem;
    private final InventorySlot equipmentSlot;
    private final ItemType itemType;
    private boolean cancelled;

    public EquipCustomItemEvent(Player player, CustomItem customItem, InventorySlot inventorySlot) {

        this.player = player;
        this.customItem = customItem;
        this.equipmentSlot = inventorySlot;
        this.itemType = customItem.getType();
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
