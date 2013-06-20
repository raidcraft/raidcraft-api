package de.raidcraft.api.items.attachments;

import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.CustomItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface ItemAttachment {

    public void applyAttachment(CustomItemStack item, Player player, ConfigurationSection args) throws CustomItemException;

    public void removeAttachment(CustomItemStack item, Player player, ConfigurationSection args) throws CustomItemException;
}
