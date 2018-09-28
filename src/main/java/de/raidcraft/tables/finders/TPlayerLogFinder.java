package de.raidcraft.tables.finders;

import de.raidcraft.tables.TPlayerLog;
import io.ebean.Finder;

public class TPlayerLogFinder extends Finder<Integer, TPlayerLog> {

    /**
     * Construct using the default EbeanServer.
     */
    public TPlayerLogFinder() {
        super(TPlayerLog.class);
    }

}
