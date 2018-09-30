package de.raidcraft.util;

import de.raidcraft.api.Component;
import org.bukkit.block.Block;

public interface BlockTracker extends Component {

    void addPlayerPlacedBlock(Block block);

    boolean isPlayerPlacedBlock(Block block);
}
