package de.raidcraft.util.ItemUtils.serialazition;

import org.bukkit.inventory.ItemStack;

/**
 * @author Philip
 */
public interface Serializable {

    public String serialize();
    public ItemStack deserialize(String serializedData);
}
