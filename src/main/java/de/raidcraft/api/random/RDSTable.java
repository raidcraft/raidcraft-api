package de.raidcraft.api.random;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface RDSTable extends RDSObject {

    /**
     * Gets how many items should drop from this table.
     *
     * @return maximum of dropped items
     */
    int getCount();

    /**
     * Sets the count of items that should drop from this table.
     * The result may be less but never more than the set count.
     *
     * @param count to drop
     */
    void setCount(int count);

    /**
     * Gets the content of the table that will be evaluated when querying for the result.
     * THe content can also be other table objects and they may contain more tables.
     * Can recurse indefinetly.
     *
     * @return contents of the table
     */
    Collection<RDSObject> getContents();

    RDSTable addEntry(RDSObject object);

    RDSTable addEntry(RDSObject object, double probability);

    RDSTable addEntry(RDSObject object, double probability, boolean enabled, boolean always, boolean unique);

    /**
     * Gets the result returned by this tunning random calculations.
     *
     * @return calculated random result
     */
    Collection<RDSObject> getResult();
}
