package de.raidcraft.api.random;

import lombok.Data;

/**
 * @author Silthus
 */
@Data
public abstract class AbstractRDSObject implements RDSObject {

    private boolean enabled;
    private boolean always;
    private boolean unique;
    private double probability;
    private RDSTable table;

    public AbstractRDSObject() {

        this(0);
    }

    public AbstractRDSObject(double probability) {

        this(probability, true, false, false);
    }

    public AbstractRDSObject(double probability, boolean enabled, boolean always, boolean unique) {

        this.probability = probability;
        this.enabled = enabled;
        this.always = always;
        this.unique = unique;
    }

}
