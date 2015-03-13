package de.raidcraft.api.random;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GenericRDSTable extends GenericRDSObject implements RDSTable {

    private final Collection<RDSObject> contents;
    private int count;

    public GenericRDSTable() {

        this(null, 1, 1);
    }

    public GenericRDSTable(Collection<RDSObject> contents, int count, double probability) {

        this(contents, count, probability, true, false, false);
    }

    public GenericRDSTable(Collection<RDSObject> contents, int count, double probability, boolean enabled, boolean always, boolean unique) {

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

    private Collection<RDSObject> uniqueDrops = new HashSet<>();

    private void addToResult(Collection<RDSObject> result, RDSObject object) {

        if (!object.isUnique() || !uniqueDrops.contains(object))
        {
            if (object.isUnique()) {
                uniqueDrops.add(object);
            }

            if (!(object instanceof RDSNullValue)) {
                if (object instanceof RDSTable) {
                    result.addAll(((RDSTable) object).getResult());
                } else {
                    // INSTANCECHECK
                    // Check if the object to add implements IRDSObjectCreator.
                    // If it does, call the CreateInstance() method and add its return value
                    // to the result set. If it does not, add the object o directly.
                    RDSObject adder = object;
                    if (object instanceof RDSObjectCreator)
                    adder = ((RDSObjectCreator)object).createInstance();

                    result.add(adder);
                    object.onHit();
                }
            } else {
                object.onHit();
            }
        }
    }

    @Override
    public Collection<RDSObject> getResult() {

        // The return value, a list of hit objects
        List<RDSObject> result = new ArrayList<>();
        uniqueDrops = new HashSet<>();

        // Do the PreEvaluation on all objects contained in the current table
        // This is the moment where those objects might disable themselves.
        getContents().forEach(RDSObject::onPreResultEvaluation);

        // Add all the objects that are hit "Always" to the result
        // Those objects are really added always, no matter what "Count"
        // is set in the table! If there are 5 objects "always", those 5 will
        // drop, even if the count says only 3.
        getContents().stream()
                .filter(entry -> entry.isAlways() && entry.isEnabled())
                .forEach(entry -> addToResult(result, entry));

        // Now calculate the real dropcount, this is the table's count minus the
        // number of Always-drops.
        // It is possible, that the remaining drops go below zero, in which case
        // no other objects will be added to the result here.
        long alwaysCount = getContents().stream()
                .filter(entry -> entry.isAlways() && entry.isEnabled())
                .count();
        long realDropCount = getCount() - alwaysCount;

        // Continue only, if there is a Count left to be processed
        if (realDropCount > 0)
        {
            for (int dropCount = 0; dropCount < realDropCount; dropCount++)
            {
                // Find the objects, that can be hit now
                // This is all objects, that are Enabled and that have not already been added through the Always flag
                Collection<RDSObject> dropables = getContents().stream()
                        .filter(entry -> entry.isEnabled() && !entry.isAlways())
                        .collect(Collectors.toList());

                // This is the magic random number that will decide, which object is hit now
                double hitValue = RDSRandom.getDoubleValue(dropables.stream().mapToDouble(RDSObject::getProbability).sum());

                // Find out in a loop which object's probability hits the random value...
                double runningValue = 0;
                for (RDSObject object : dropables)
                {
                    // Count up until we find the first item that exceeds the hitvalue...
                    runningValue += object.getProbability();
                    if (hitValue < runningValue)
                    {
                        // ...and the oscar goes too...
                        addToResult(result, object);
                        break;
                    }
                }
            }
        }

        // Now give all objects in the result set the chance to interact with
        // the other objects in the result set.
        for (RDSObject object : result) {
            object.onPostResultEvaluation(result);
        }

        // Return the set now
        return result;
    }
}
