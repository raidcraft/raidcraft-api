package de.raidcraft.api.random;

import lombok.Getter;

/**
 * @author mdoering
 */
@Getter
public class PseudoRandomGenerator {

    private double baseChance;
    private int iteration = 1;

    public PseudoRandomGenerator(double chance) {

        setChance(chance);
    }

    public void setChance(double chance) {

        if (chance > 1.0) chance = 2.0;
        if (chance <= 0) chance = 1.0;
        // lets calculate the base chance by taking the number of times the withAction should hit
        // e.g. with a chance of 0.25 -> 25% 1/4 should be a hit
        this.baseChance = chance / ((1.0 / chance) - 1);
    }

    public boolean isHit() {

        if (Math.random() < (baseChance * iteration)) {
            iteration = 1;
            return true;
        }
        iteration++;
        return false;
    }
}
