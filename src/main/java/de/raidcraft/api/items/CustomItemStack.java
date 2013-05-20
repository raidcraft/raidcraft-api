package de.raidcraft.api.items;

import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public class CustomItemStack {

    private final CustomItem item;
    private final ItemStack itemStack;

    protected CustomItemStack(CustomItem item, ItemStack itemStack) {

        this.item = item;
        this.itemStack = itemStack;
    }

    public CustomItem getItem() {

        return item;
    }

    public ItemStack getHandle() {

        return itemStack;
    }

    public void rebuild() {

        getItem().rebuild(getHandle());
    }
}
