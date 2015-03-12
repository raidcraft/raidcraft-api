package de.raidcraft.api.random;

/**
 * @author Silthus
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

    boolean isUnique();

    void setUnique(boolean unique);

    double getProbability();

    void setProbability(double probability);

    RDSTable getTable();

    void setTable(RDSTable table);

    void onPreResultEvaluation();

    void onHit();

    void onPostResultEvaluation();
}
