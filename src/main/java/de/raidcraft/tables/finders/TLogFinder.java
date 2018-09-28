package de.raidcraft.tables.finders;

import de.raidcraft.tables.TLog;
import io.ebean.Finder;

public class TLogFinder extends Finder<Integer, TLog> {

    /**
     * Construct using the default EbeanServer.
     */
    public TLogFinder() {
        super(TLog.class);
    }

}
