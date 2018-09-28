package de.raidcraft.tables.finders;

import de.raidcraft.tables.TPlugin_;
import io.ebean.Finder;

public class TPlugin_Finder extends Finder<Integer, TPlugin_> {

    /**
     * Construct using the default EbeanServer.
     */
    public TPlugin_Finder() {
        super(TPlugin_.class);
    }

}
