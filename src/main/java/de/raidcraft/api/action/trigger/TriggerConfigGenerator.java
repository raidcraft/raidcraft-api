package de.raidcraft.api.action.trigger;

import de.raidcraft.api.action.ActionAPIConfigGenerator;

/**
 * @author mdoering
 */
public interface TriggerConfigGenerator extends ActionAPIConfigGenerator {

    @Override
    default String getPath() {

        return "trigger";
    }
}
