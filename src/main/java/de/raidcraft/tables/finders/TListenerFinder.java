package de.raidcraft.tables.finders;

import de.raidcraft.tables.TListener;
import io.ebean.Finder;

public class TListenerFinder extends Finder<Integer, TListener> {

    /**
     * Construct using the default EbeanServer.
     */
    public TListenerFinder() {
        super(TListener.class);
    }

}
