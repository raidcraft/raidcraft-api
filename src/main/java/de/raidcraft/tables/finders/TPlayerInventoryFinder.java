package de.raidcraft.tables.finders;

import de.raidcraft.api.inventory.sync.TPlayerInventory;
import io.ebean.Finder;

public class TPlayerInventoryFinder extends Finder<Integer, TPlayerInventory> {

    /**
     * Construct using the default EbeanServer.
     */
    public TPlayerInventoryFinder() {
        super(TPlayerInventory.class);
    }

}
