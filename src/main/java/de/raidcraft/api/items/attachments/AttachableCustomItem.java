package de.raidcraft.api.items.attachments;

import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface AttachableCustomItem extends CustomItem {

    public void addAttachment(String name, ConfigurationSection config);

    public List<ItemAttachment> getAttachments(Player player) throws ItemAttachmentException;

    public void apply(Player player) throws CustomItemException;

    public void remove(Player player) throws CustomItemException;
}
