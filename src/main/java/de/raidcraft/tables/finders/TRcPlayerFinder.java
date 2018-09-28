package de.raidcraft.tables.finders;

import de.raidcraft.tables.TRcPlayer;
import io.ebean.Finder;

public class TRcPlayerFinder extends Finder<Integer, TRcPlayer> {

    /**
     * Construct using the default EbeanServer.
     */
    public TRcPlayerFinder() {
        super(TRcPlayer.class);
    }

}
