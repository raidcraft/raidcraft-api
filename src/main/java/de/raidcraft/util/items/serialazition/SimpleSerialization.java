package de.raidcraft.util.items.serialazition;

import org.bukkit.inventory.ItemStack;

/**
 * @author Philip
 */
public abstract class SimpleSerialization implements Serializable {

    private ItemStack item;

    public SimpleSerialization(ItemStack item) {
        this.item = item;
    }

    @Override
    abstract public String serialize();

    @Override
    abstract public ItemStack deserialize(String serializedData);

    public ItemStack getItem() {
        return item;
    }
}
