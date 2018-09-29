package de.raidcraft.tables.finders;

import de.raidcraft.tables.TPlugin;
import io.ebean.Finder;

public class TPlugin_Finder extends Finder<Integer, TPlugin> {

    /**
     * Construct using the default EbeanServer.
     */
    public TPlugin_Finder() {
        super(TPlugin.class);
    }

}
