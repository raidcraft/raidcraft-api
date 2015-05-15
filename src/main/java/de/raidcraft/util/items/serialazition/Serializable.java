package de.raidcraft.util.items.serialazition;

import org.bukkit.inventory.ItemStack;

/**
 * @author Philip
 */
@Deprecated
public interface Serializable {

    String serialize();

    ItemStack deserialize(String serializedData);
}
