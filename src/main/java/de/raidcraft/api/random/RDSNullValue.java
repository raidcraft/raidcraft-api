package de.raidcraft.api.random;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
public class RDSNullValue extends GenericRDSValue<Object> {

    @RDSObjectFactory.Name("null")
    public static class RDSNullFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new RDSNullValue(config.getDouble("probability", 1));
        }
    }

    public RDSNullValue(double probability) {

        this(probability, true, false, false);
    }

    public RDSNullValue(double probability, boolean enabled, boolean always, boolean unique) {

        super(null, probability, enabled, always, unique);
    }
}
