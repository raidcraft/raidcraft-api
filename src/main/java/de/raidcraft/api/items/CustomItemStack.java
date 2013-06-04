package de.raidcraft.api.items;

import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public class CustomItemStack {

    private final CustomItem item;
    private final ItemStack itemStack;
    private int durability;

    protected CustomItemStack(CustomItem item, ItemStack itemStack) {

        this.item = item;
        this.itemStack = itemStack;
        if (item instanceof CustomEquipment) {
            this.durability = ((CustomEquipment) item).parseDurability(itemStack);
        }
    }

    public int getDurability() {

        return durability;
    }

    public void setDurability(int durability) {

        this.durability = durability;
        if (getItem() instanceof CustomEquipment) {
            ((CustomEquipment) getItem()).updateDurability(getHandle(), durability);
        }
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
