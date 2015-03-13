package de.raidcraft.api.random;

/**
 * @author mdoering
 */
public class RDSNullValue extends GenericRDSValue<Object> {

    public RDSNullValue(double probability) {

        this(probability, true, false, false);
    }

    public RDSNullValue(double probability, boolean enabled, boolean always, boolean unique) {

        super(null, probability, enabled, always, unique);
    }
}
