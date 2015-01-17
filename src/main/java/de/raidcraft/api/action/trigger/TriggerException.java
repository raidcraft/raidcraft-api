package de.raidcraft.api.action.trigger;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class TriggerException extends RaidCraftException {

    public TriggerException(String message) {

        super(message);
    }

    public TriggerException(String message, Throwable cause) {

        super(message, cause);
    }

    public TriggerException(Throwable cause) {

        super(cause);
    }

    public TriggerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }

}
