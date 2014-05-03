package de.raidcraft.api.achievement;

import de.raidcraft.api.action.trigger.TriggerListener;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Represents the achievement attached to the achievement holder (e.g. player).
 * An achievement is created from an {@link AchievementTemplate} when it interacts with
 * a valid {@link AchievementHolder}. At this point it should always be saved in a
 * database, even if the achievement was not yet unlocked.
 * The progress of the achivement is also saved in this object.
 */
public interface Achievement<T> extends TriggerListener<T> {

    @Override
    public default T getTriggerEntityType() {

        return getHolder().getType();
    }

    /**
     * @see AchievementTemplate#getIdentifier()
     */
    public default String getIdentifier() {

        return getTemplate().getIdentifier();
    }

    /**
     * @see AchievementTemplate#getDisplayName()
     */
    public default String getDisplayName() {

        return getTemplate().getDisplayName();
    }

    /**
     * Gets the holder of the achievement the template is assigned to.
     *
     * @see AchievementHolder
     * @return achievement holder
     */
    public AchievementHolder<T> getHolder();

    /**
     * Gets the template of the achievement. The template holds all the information
     * and is loaded once per config file.
     *
     * @see AchievementTemplate
     * @return achievement template
     */
    public AchievementTemplate getTemplate();

    /**
     * Checks if the achievement is active and needs to be checked for requirements
     * and trigger. Achievements that are completed are removed from the active list.
     *
     * @return list of active achievements
     */
    public default boolean isActive() {

        return getGainedDate() == null || getGainedDate().before(Timestamp.from(Instant.now()));
    }

    /**
     * Checks if the achievements was completed and is no longer active.
     *
     * @return true if achievement was completed
     */
    public default boolean isGained() {

        return getGainedDate() != null && getGainedDate().after(Timestamp.from(Instant.now()));
    }

    /**
     * Gets the time the achievement was gained. Can be null if the achievement
     * is still active and not yet finished.
     *
     * @return completion time of the achievement. can be null if not completed
     */
    public Timestamp getGainedDate();

    public void unlock();

    public void remove();

    public void save();

    public void delete();
}
