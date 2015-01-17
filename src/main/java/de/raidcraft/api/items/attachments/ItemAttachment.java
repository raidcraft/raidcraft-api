package de.raidcraft.api.items.attachments;

import de.raidcraft.api.items.CustomItemException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface ItemAttachment {

    public void loadAttachment(ConfigurationSection data);

    public void applyAttachment(Player player) throws CustomItemException;

    public void removeAttachment(Player player) throws CustomItemException;
}
