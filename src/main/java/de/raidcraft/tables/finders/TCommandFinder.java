package de.raidcraft.tables.finders;

import de.raidcraft.tables.TCommand;
import io.ebean.Finder;

public class TCommandFinder extends Finder<Integer, TCommand> {

    /**
     * Construct using the default EbeanServer.
     */
    public TCommandFinder() {
        super(TCommand.class);
    }

}
