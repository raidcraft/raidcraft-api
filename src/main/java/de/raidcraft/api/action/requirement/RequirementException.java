package de.raidcraft.api.action.requirement;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class RequirementException extends RaidCraftException {

    public RequirementException(String message) {

        super(message);
    }

    public RequirementException(String message, Throwable cause) {

        super(message, cause);
    }

    public RequirementException(Throwable cause) {

        super(cause);
    }

    public RequirementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }

}
