package de.raidcraft.api.random.objects;

import de.raidcraft.api.random.GenericRDSValue;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectFactory;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author mdoering
 */
public class MoneyLootObject extends GenericRDSValue<Double> {

    @RDSObjectFactory.Name("money")
    public static class MoneyLootFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new MoneyLootObject(config.getDouble("amount"));
        }
    }

    public MoneyLootObject(double amount) {

        super(amount, 1);
    }
}
