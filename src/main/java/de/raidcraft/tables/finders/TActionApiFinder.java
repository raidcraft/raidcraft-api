package de.raidcraft.tables.finders;

import de.raidcraft.tables.TActionApi;
import io.ebean.Finder;

public class TActionApiFinder extends Finder<Integer, TActionApi> {

    /**
     * Construct using the default EbeanServer.
     */
    public TActionApiFinder() {
        super(TActionApi.class);
    }

}
