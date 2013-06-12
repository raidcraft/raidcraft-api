package de.raidcraft.api.items.attachments;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface UseableItemAttachment extends ItemAttachment {

    public void use(UseableCustomItem item, Player player, ConfigurationSection args) throws ItemAttachmentException;
}
