package de.raidcraft.tables.finders;

import de.raidcraft.api.storage.TObjectStorage;
import io.ebean.Finder;

public class TObjectStorageFinder extends Finder<Integer, TObjectStorage> {

    /**
     * Construct using the default EbeanServer.
     */
    public TObjectStorageFinder() {
        super(TObjectStorage.class);
    }

}
