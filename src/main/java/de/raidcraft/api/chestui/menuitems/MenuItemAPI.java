package de.raidcraft.api.chestui.menuitems;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Dragonfire
 */
public abstract class MenuItemAPI {

    @Getter
    @Setter
    private int slot;
    @Getter
    @Setter
    private Inventory inventory;

    private ItemStack item;

    public void setItem(ItemStack item) {

        this.item = item;
        if (inventory != null) {
            inventory.setItem(slot, this.item);
        }
    }

    public ItemStack getItem() {

        if (inventory == null) {
            return item;
        }
        return inventory.getItem(slot);
    }

    public abstract void trigger(Player player);
}
