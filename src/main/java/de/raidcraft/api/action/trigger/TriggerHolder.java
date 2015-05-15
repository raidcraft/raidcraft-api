package de.raidcraft.api.action.trigger;

import de.raidcraft.api.action.TriggerFactory;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface TriggerHolder {

    Collection<TriggerFactory> getTrigger();
}
