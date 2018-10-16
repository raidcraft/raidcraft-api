package de.raidcraft.api.player;

import de.raidcraft.api.Component;
import org.bukkit.entity.Player;

public interface GhostManager extends Component {

    boolean isGhost(Player player);
}
