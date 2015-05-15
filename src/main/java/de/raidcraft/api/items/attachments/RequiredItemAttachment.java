package de.raidcraft.api.items.attachments;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface RequiredItemAttachment extends ItemAttachment {

    String getName();

    boolean isRequirementMet(Player player);

    String getItemText();

    String getErrorMessage();
}
