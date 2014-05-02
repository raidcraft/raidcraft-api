package de.raidcraft.api.achievement;

/**
 * @author mdoering
 */
public class DuplicateAchievementException extends AchievementException {

    public DuplicateAchievementException(String message) {

        super(message);
    }

    public DuplicateAchievementException(String message, Throwable cause) {

        super(message, cause);
    }

    public DuplicateAchievementException(Throwable cause) {

        super(cause);
    }

    public DuplicateAchievementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }
}
