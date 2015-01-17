package de.raidcraft.api.items.attachments;

import de.raidcraft.api.items.CustomItemStack;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface UseableCustomItem extends AttachableCustomItem {

    public void use(Player player, CustomItemStack itemStack) throws ItemAttachmentException;
}
