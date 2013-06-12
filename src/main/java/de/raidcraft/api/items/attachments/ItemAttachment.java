package de.raidcraft.api.items.attachments;

import de.raidcraft.api.items.CustomItemException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface ItemAttachment {

    public void applyAttachment(AttachableCustomItem item, Player player, ConfigurationSection args) throws CustomItemException;

    public void removeAttachment(AttachableCustomItem item, Player player, ConfigurationSection args) throws CustomItemException;
}
