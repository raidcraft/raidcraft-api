package de.raidcraft.tables.finders;

import de.raidcraft.api.inventory.TPersistentInventorySlot;
import io.ebean.Finder;

public class TPersistentInventorySlotFinder extends Finder<Integer, TPersistentInventorySlot> {

    /**
     * Construct using the default EbeanServer.
     */
    public TPersistentInventorySlotFinder() {
        super(TPersistentInventorySlot.class);
    }

}
