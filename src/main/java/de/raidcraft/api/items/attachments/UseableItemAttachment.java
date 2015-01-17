package de.raidcraft.api.items.attachments;

import de.raidcraft.api.items.CustomItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface UseableItemAttachment extends ItemAttachment {

    public void use(CustomItemStack item, Player player, ConfigurationSection args) throws ItemAttachmentException;
}
