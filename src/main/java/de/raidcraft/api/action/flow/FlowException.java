package de.raidcraft.api.action.flow;

import de.raidcraft.api.RaidCraftException;

/**
 * @author mdoering
 */
public class FlowException extends RaidCraftException {

    public FlowException(String message) {

        super(message);
    }

    public FlowException(String message, Throwable cause) {

        super(message, cause);
    }

    public FlowException(Throwable cause) {

        super(cause);
    }

    public FlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }
}
