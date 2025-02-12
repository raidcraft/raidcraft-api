package de.raidcraft.api.random;

import de.raidcraft.api.action.requirement.Requirement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Data
public class GenericRDSObject implements RDSObject {

    private String type;
    private boolean enabled;
    private boolean always;
    private boolean excludeFromRandom;
    private boolean unique;
    private double probability;
    private RDSTable table;
    private List<Requirement<?>> requirements = new ArrayList<>();

    public GenericRDSObject() {

        this(0);
    }

    public GenericRDSObject(double probability) {

        this(probability, true, false, false);
    }

    public GenericRDSObject(double probability, boolean enabled, boolean always, boolean unique) {

        this.probability = probability;
        this.enabled = enabled;
        this.always = always;
        this.unique = unique;
    }
}
