package de.raidcraft.api.random;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Silthus
 */
public abstract class AbstractRDSTable extends AbstractRDSObject implements RDSTable {

    private final Collection<RDSObject> contents;
    private int count;

    public AbstractRDSTable() {

        this(null, 1, 1);
    }

    public AbstractRDSTable(Collection<RDSObject> contents, int count, double probability) {

        this(contents, count, probability, true, false, false);
    }

    public AbstractRDSTable(Collection<RDSObject> contents, int count, double probability, boolean enabled, boolean always, boolean unique) {

        super(probability, enabled, always, unique);
        if (contents == null) {
            this.contents = new ArrayList<>();
        } else {
            this.contents = contents;
        }
        this.count = count;
    }

    @Override
    public RDSTable addEntry(RDSObject object) {

        this.contents.add(object);
        object.setTable(this);
        return this;
    }

    @Override
    public RDSTable addEntry(RDSObject object, double probability) {

        addEntry(object);
        object.setProbability(probability);
        return this;
    }

    @Override
    public RDSTable addEntry(RDSObject object, double probability, boolean enabled, boolean always, boolean unique) {

        addEntry(object, probability);
        object.setEnabled(enabled);
        object.setAlways(always);
        object.setUnique(unique);
        return this;
    }

    @Override
    public Collection<RDSObject> getResult() {

        return new ArrayList<>();
    }
}
