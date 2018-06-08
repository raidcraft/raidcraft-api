package de.raidcraft.api.conversations.host;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerHost extends AbstractConversationHost<Player> {

    public PlayerHost(Player player) {

        super(player.getUniqueId(), null, player);
    }

    @Override
    public Location getLocation() {

        return getType().getLocation();
    }

    @Override
    public void delete() {

        throw new UnsupportedOperationException("Cannot delete player host!");
    }
}
