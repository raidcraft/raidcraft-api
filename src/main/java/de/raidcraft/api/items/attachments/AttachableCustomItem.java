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

    void addAttachment(ConfiguredAttachment attachment);

    List<ItemAttachment> getAttachments(Player player) throws ItemAttachmentException;

    void apply(Player player, CustomItemStack itemStack, boolean loadOnly) throws CustomItemException;

    void remove(Player player, CustomItemStack itemStack) throws CustomItemException;
}
