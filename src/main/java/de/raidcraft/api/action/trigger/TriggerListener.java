package de.raidcraft.api.action.trigger;

/**
 * @author Silthus
 */
public interface TriggerListener<T> {

    public T getTriggerEntityType();

    public void processTrigger();
}
