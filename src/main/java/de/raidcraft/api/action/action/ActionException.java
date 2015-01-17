package de.raidcraft.api.action.action;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class ActionException extends RaidCraftException {

    public ActionException(String message) {

        super(message);
    }

    public ActionException(String message, Throwable cause) {

        super(message, cause);
    }

    public ActionException(Throwable cause) {

        super(cause);
    }

    public ActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }

}
