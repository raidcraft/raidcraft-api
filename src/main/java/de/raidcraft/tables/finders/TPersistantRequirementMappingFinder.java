package de.raidcraft.tables.finders;

import de.raidcraft.api.action.requirement.tables.TPersistantRequirementMapping;
import io.ebean.Finder;

public class TPersistantRequirementMappingFinder extends Finder<Integer, TPersistantRequirementMapping> {

    /**
     * Construct using the default EbeanServer.
     */
    public TPersistantRequirementMappingFinder() {
        super(TPersistantRequirementMapping.class);
    }

}
