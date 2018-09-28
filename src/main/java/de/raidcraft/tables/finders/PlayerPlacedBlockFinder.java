package de.raidcraft.tables.finders;

import de.raidcraft.tables.PlayerPlacedBlock;
import io.ebean.Finder;

public class PlayerPlacedBlockFinder extends Finder<Integer, PlayerPlacedBlock> {

    /**
     * Construct using the default EbeanServer.
     */
    public PlayerPlacedBlockFinder() {
        super(PlayerPlacedBlock.class);
    }

}
