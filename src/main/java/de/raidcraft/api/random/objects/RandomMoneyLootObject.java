package de.raidcraft.api.random.objects;

import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.api.random.RDSRandom;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author mdoering
 */
@Getter
@Setter
public class RandomMoneyLootObject extends MoneyLootObject {

    @RDSObjectFactory.Name("random-money")
    public static class RandomMoneyLootFactory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new RandomMoneyLootObject(config.getDouble("min", 0), config.getDouble("max", 0), config.getString("reason"));
        }
    }

    private double min;
    private double max;

    public RandomMoneyLootObject(double min, double max, String reason) {

        super(min, reason);
        this.min = min;
        this.max = max >= min ? max : min;
    }

    @Override
    public Optional<Double> getValue() {

        return Optional.of(RDSRandom.getDoubleValue(min, max));
    }
}
