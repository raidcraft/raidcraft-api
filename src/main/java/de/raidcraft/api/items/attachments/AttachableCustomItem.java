package de.raidcraft.api.items.attachments;

import de.raidcraft.api.items.CustomItem;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface AttachableCustomItem extends CustomItem {

    public String getProviderName();

    public String getAttachmentName();

    public void apply(Player player);

    public void remove(Player player);
}
