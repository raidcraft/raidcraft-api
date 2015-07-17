package de.raidcraft.api.random;

import java.util.Collection;

/**
 * This interface contains the properties an object must have to be a valid rds result object.
 */
public interface RDSObject {

    /**
     * Whether this object shall be looted or not.
     *
     * @return true if object can be looted
     */
    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isAlways();

    void setAlways(boolean always);

    boolean isExcludeFromRandom();

    void setExcludeFromRandom(boolean excludeFromRandom);

    boolean isUnique();

    void setUnique(boolean unique);

    double getProbability();

    void setProbability(double probability);

    RDSTable getTable();

    void setTable(RDSTable table);

    /**
     * Occurs before all the probabilities of all items of the current RDSTable are summed up together.
     * This is the moment to modify any settings immediately before a result is calculated.
     */
    default void onPreResultEvaluation() {}

    /**
     * Occurs when this RDSObject has been hit by the Result procedure.
     * (This means, this object will be part of the result set).
     */
    default void onHit() {}

    /**
     * ccurs after the result has been calculated and the result set is complete, but before
     * the RDSTable's Result method exits.
     */
    default void onPostResultEvaluation(Collection<RDSObject> result) {}
}
