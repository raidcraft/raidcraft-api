package de.raidcraft.api.achievement;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * The AchievementHolder is the one that actually gains the achievements. The holder
 * can be a player but can also be something else like a guild. The type must be defined
 * by an implementing because some requirements and trigger may only take specific holder types.
 */
public interface AchievementHolder<T> {

    /**
     * Gets the friendly display name of the holder as it is displayed
     * to players and in chat messages.
     *
     * @return friendly display name
     */
    public String getDisplayName();

    /**
     * Gets the type of the achievement holder. This can be anything
     * ranging from a player to guilds. The type defines what kind of
     * requirements and triggers the achievement can have.
     *
     * @return holder type
     */
    public T getType();

    public default boolean hasAchievement(String identifier) {

        return getAchievements().parallelStream().anyMatch(
                achievement -> achievement.getIdentifier().equals(identifier)
        );
    }

    /**
     * Checks if the holder has the given achievement based on the template.
     * Will check finished and active achievements.
     *
     * @param template to check
     * @return true if holder has achievement
     */
    public default boolean hasAchievement(AchievementTemplate template) {

        return getAchievements().parallelStream().anyMatch(
                achievement -> achievement.getTemplate().equals(template)
        );
    }

    /**
     * Checks if the holder has gained the given achievement.
     *
     * @param template to check
     *
     * @return true if {@link #getGainedAchievements()} contains template
     */
    public default boolean hasGainedAchievement(AchievementTemplate template) {

        return getGainedAchievements().parallelStream().anyMatch(
                achievement -> achievement.getTemplate().equals(template)
        );
    }

    /**
     * Checks if the holder has an active achievement of the given template.
     * Will not return true if the achievement is already finished and archived.
     *
     * @param template to check
     * @return true if {@link #getActiveAchievements()} contains template
     */
    public default boolean hasActiveAchievement(AchievementTemplate template) {

        return getActiveAchievements().parallelStream().anyMatch(
                achievement -> achievement.getTemplate().equals(template)
        );
    }

    /**
     * Gets a list of all active achievements. Does not contain achievements
     * that are gained and there for finished.
     * For a list of all achievements call {@link #getAchievements()}
     *
     * @return list of active achievements
     */
    public default Collection<Achievement<T>> getActiveAchievements() {

        return getAchievements().parallelStream()
                .filter(Achievement::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of finished and gained achievements. Does not contain
     * active achievements that are not yet finished.
     * For a list of all achievements call {@link #getAchievements()}
     *
     * @return list of gained achievements
     */
    public default Collection<Achievement<T>> getGainedAchievements() {

        return getAchievements().parallelStream()
                .filter(Achievement::isGained)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all achievements that this holder has gained
     * and that are currently active and not yet finished.
     *
     * @return list of all gained and active achievements
     */
    public Collection<Achievement<T>> getAchievements();

    /**
     * Adds the given achievement template to the holder as an {@link de.raidcraft.api.achievement.Achievement}.
     * This will mark the achievement as active and start checking requirements.
     * If the holder already has the achievement but it is inactive, because it was removed, it will
     * be reactivated. If the achievement is already active or gained nothing will happen.
     *
     * @param template to add as achievement
     */
    public void addAchievement(AchievementTemplate template);

    /**
     * Removes the given achievement from the holder marking it as inactive.
     * This may also reset the achievement if the holder already completed it.
     * Can return null if the holder has no achievement of this template.
     *
     * @param template achievement to remove
     * @return null if achievement did not exists
     */
    public Achievement<T> removeAchievement(AchievementTemplate template);

    /**
     * Removes the given achievement from the holder.
     * @see #removeAchievement(AchievementTemplate)
     *
     * @param achievement to remove
     * @return null if achievement did not exists
     */
    public default Achievement<T> removeAchievement(Achievement<T> achievement) {

        return removeAchievement(achievement.getTemplate());
    }
}
