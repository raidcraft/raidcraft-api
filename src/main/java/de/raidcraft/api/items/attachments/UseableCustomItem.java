package de.raidcraft.api.items.attachments;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface UseableCustomItem extends AttachableCustomItem {

    public void use(Player player) throws ItemAttachmentException;
}
