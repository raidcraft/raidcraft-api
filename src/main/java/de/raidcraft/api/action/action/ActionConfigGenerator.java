package de.raidcraft.api.action.action;

import de.raidcraft.api.action.ActionAPIConfigGenerator;

/**
 * @author mdoering
 */
public interface ActionConfigGenerator extends ActionAPIConfigGenerator {

    @Override
    default String getPath() {

        return "actions";
    }
}
