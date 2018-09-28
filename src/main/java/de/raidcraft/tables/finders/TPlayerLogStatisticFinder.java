package de.raidcraft.tables.finders;

import de.raidcraft.tables.TPlayerLogStatistic;
import io.ebean.Finder;

public class TPlayerLogStatisticFinder extends Finder<Integer, TPlayerLogStatistic> {

    /**
     * Construct using the default EbeanServer.
     */
    public TPlayerLogStatisticFinder() {
        super(TPlayerLogStatistic.class);
    }

}
