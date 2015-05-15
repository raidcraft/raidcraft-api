package de.raidcraft.api.items.attachments;

import de.raidcraft.api.items.CustomItemException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface ItemAttachment {

    void loadAttachment(ConfigurationSection data);

    void applyAttachment(Player player) throws CustomItemException;

    void removeAttachment(Player player) throws CustomItemException;
}
