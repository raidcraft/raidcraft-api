package de.raidcraft.api.random;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * This interface describes a table of IRDSObjects. One (or more) of them is/are picked as the result set.
 */
public interface RDSTable extends RDSObject {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Name {

        String value();
    }

    /**
     * The maximum number of entries expected in the Result. The final count of items in the result may be lower
     * if some of the entries may return a null result (no drop).
     *
     * @return maximum number of entries expected in the result
     */
    int getCount();

    /**
     * Sets the maximum number of entries expected in the Result. The final count of items in the result may be lower
     * if some of the entries may return a null result (no drop).
     *
     * @param count of the maximum entries to expected in the result
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
     * Gets the result. Calling this method will start the random pick process and generate the result.
     * This result remains constant for the lifetime of this table object.
     * Use the ResetResult method to clear the result and create a new one.
     *
     * @return calculated random result of this table
     */
    Collection<RDSObject> getResult();
}
