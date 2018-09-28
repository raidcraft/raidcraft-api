package de.raidcraft.tables.finders;

import de.raidcraft.api.action.requirement.tables.TPersistantRequirement;
import io.ebean.Finder;

public class TPersistantRequirementFinder extends Finder<Integer, TPersistantRequirement> {

    /**
     * Construct using the default EbeanServer.
     */
    public TPersistantRequirementFinder() {
        super(TPersistantRequirement.class);
    }

}
