package de.raidcraft.tables.finders;

import de.raidcraft.api.inventory.TPersistentInventory;
import io.ebean.Finder;

public class TPersistentInventoryFinder extends Finder<Integer, TPersistentInventory> {

    /**
     * Construct using the default EbeanServer.
     */
    public TPersistentInventoryFinder() {
        super(TPersistentInventory.class);
    }

}
