package de.raidcraft.api.items.attachments;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface UseableCustomItem extends AttachableCustomItem {

    public UseableItemAttachment getUseableAttachment();

    public double getCooldown();

    public double getRemainingCooldown();

    public boolean isOnCooldown();

    public void use(Player player) throws ItemAttachmentException;
}
