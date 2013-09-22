package de.raidcraft.api.items.attachments;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface RequiredItemAttachment extends ItemAttachment {

    public String getName();

    public boolean isRequirementMet(Player player);

    public String getItemText(Player player);

    public String getErrorMessage(Player player);
}
