package de.raidcraft.util.items.serialazition;

import org.bukkit.inventory.ItemStack;

/**
 * @author Philip
 */
@Deprecated
public interface Serializable {

    public String serialize();
    public ItemStack deserialize(String serializedData);
}
