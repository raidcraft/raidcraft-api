package de.raidcraft.api.action.trigger;

/**
 * @author Silthus
 */
public interface TriggerListener<T> {

    public default String getListenerId() {

        return "GLOBAL";
    }

    public Class<T> getTriggerEntityType();

    /**
     * Passes the entity to process the trigger. Should return true if processing was
     * successful and if the actions should be executed. False if no actions should
     * be executed and the processing failed.
     *
     * @param entity to process
     *
     * @return true if actions should execute
     */
    public boolean processTrigger(T entity);
}
