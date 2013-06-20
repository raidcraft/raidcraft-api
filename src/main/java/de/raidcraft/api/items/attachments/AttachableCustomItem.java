package de.raidcraft.api.items.attachments;

import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.CustomItemStack;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface AttachableCustomItem extends CustomItem {

    public void addAttachment(ConfiguredAttachment attachment);

    public List<ItemAttachment> getAttachments(Player player) throws ItemAttachmentException;

    public void apply(Player player, CustomItemStack itemStack) throws CustomItemException;

    public void remove(Player player, CustomItemStack itemStack) throws CustomItemException;
}
