package de.raidcraft.api.items.attachments;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface ItemAttachmentProvider {

    public ItemAttachment getItemAttachment(Player player, String attachmentName) throws ItemAttachmentException;
}
