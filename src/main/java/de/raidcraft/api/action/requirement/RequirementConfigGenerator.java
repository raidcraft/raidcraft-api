package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.ActionAPIConfigGenerator;

/**
 * @author mdoering
 */
public interface RequirementConfigGenerator extends ActionAPIConfigGenerator {

    @Override
    default String getPath() {

        return "requirements";
    }
}
