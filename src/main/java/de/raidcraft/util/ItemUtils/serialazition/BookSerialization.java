package de.raidcraft.util.itemutils.serialazition;

import org.bukkit.inventory.ItemStack;

/**
 * @author Philip
 */
public class BookSerialization extends SimpleSerialization {

    public BookSerialization(ItemStack item) {
        super(item);
    }

    @Override
    public String serialize() {
        //TODO implement
        return "";
    }

    @Override
    public ItemStack deserialize(String serializedData) {
        //TODO implement
        return getItem();
    }
}
