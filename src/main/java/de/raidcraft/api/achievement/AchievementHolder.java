package de.raidcraft.api.achievement;

import lombok.NonNull;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The AchievementHolder is the one that actually gains the achievements. The holder
 * can be a player but can also be something else like a guild. The type must be defined
 * by an implementing because some requirements and trigger may only take specific holder types.
 */
public interface AchievementHolder<T> {

    public UUID getUniqueIdentifier();

    /**
     * Gets the friendly display name of the holder as it is displayed
     * to players and in chat messages.
     *
     * @return friendly display name
     */
    public String getDisplayName();

    /**
     * Gets the total amount of achievement points for this holder.
     * Only counts completed achievements.
     *
     * @return sum of achievement points for completed achievements
     */
    public int getTotalPoints();

    /**
     * Gets the type of the achievement holder. This can be anything
     * ranging from a player to guilds. The type defines what kind of
     * requirements and triggers the achievement can have.
     *
     * @return holder type
     */
    public T getType();

    /**
     * Checks if the holder has the given permission.
     *
     * @param permission to check
     *
     * @return true if holder has the permission
     */
    public boolean hasPermission(String permission);

    /**
     * Checks if the holder has an achievement with the given identifier.
     * This does not mean the achievement is complete or active.
     *
     * @param identifier to check for
     *
     * @return true if achievement exists for the holder
     */
    public default boolean hasAchievement(@NonNull String identifier) {

        return getAchievements().parallelStream().anyMatch(
                achievement -> achievement.getIdentifier().equals(identifier)
        );
    }

    /**
     * Checks if the holder has the given achievement based on the template.
     * Will check finished and active achievements.
     *
     * @param template to check
     *
     * @return true if holder has achievement
     */
    public default boolean hasAchievement(@NonNull AchievementTemplate template) {

        return getAchievements().parallelStream().anyMatch(
                achievement -> achievement.getTemplate().equals(template)
        );
    }

    public default boolean hasGainedAchievement(@NonNull String identifier) {

        return getCompletedAchievements().parallelStream()
                .filter(achievement -> achievement.getIdentifier().equals(identifier.toLowerCase()))
                .anyMatch(Achievement::isCompleted);
    }

    /**
     * Checks if the holder has gained the given achievement.
     *
     * @param template to check
     *
     * @return true if {@link #getCompletedAchievements()} contains template
     */
    public default boolean hasGainedAchievement(@NonNull AchievementTemplate template) {

        return getCompletedAchievements().parallelStream()
                .filter(achievement -> achievement.getTemplate().equals(template))
                .anyMatch(achievement -> achievement.getCompletionDate() != null);
    }

    /**
     * Checks if the holder has an active achievement of the given template.
     * Will not return true if the achievement is already finished and archived.
     *
     * @param template to check
     *
     * @return true if {@link #getActiveAchievements()} contains template
     */
    public default boolean hasActiveAchievement(@NonNull AchievementTemplate template) {

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
    public default Collection<Achievement<T>> getCompletedAchievements() {

        return getAchievements().parallelStream()
                .filter(Achievement::isCompleted)
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
     * Gets the given achievement based on the unique identifier string.
     * Can return null if no achievement is found.
     *
     * @param identifier to check for
     *
     * @return null if no achievement is found
     */
    public default Achievement<T> getAchievement(@NonNull String identifier) {

        return getAchievements().stream()
                .filter(achievement -> achievement.getTemplate().getIdentifier().equals(identifier.toLowerCase()))
                .findAny().get();
    }

    /**
     * Gets the given achievement based on the achievement template.
     * Can return null if no achievement is found.
     *
     * @param template to check for
     *
     * @return null if no achievement is found
     */
    public default Achievement<T> getAchievement(@NonNull AchievementTemplate template) {

        return getAchievements().stream()
                .filter(achievement -> achievement.getTemplate().equals(template))
                .findAny().get();
    }

    /**
     * Adds the given achievement template to the holder as an {@link de.raidcraft.api.achievement.Achievement}.
     * This will mark the achievement as active and start checking requirements.
     * If the holder already has the achievement but it is inactive, because it was removed, it will
     * be reactivated. If the achievement is already active or gained nothing will happen.
     *
     * @param template to add as achievement
     */
    public Achievement<T> addAchievement(@NonNull AchievementTemplate template);

    /**
     * Adds the already created achievement to the holder. This will check what state the
     * achievement has and will not restart the achievement process.
     *
     * @param achievement to add
     */
    public Achievement<T> addAchievement(@NonNull Achievement<T> achievement);

    /**
     * Removes the given achievement from the holder marking it as inactive.
     * This may also reset the achievement if the holder already completed it.
     * Can return null if the holder has no achievement of this template.
     *
     * @param template achievement to remove
     *
     * @return null if achievement did not exists
     */
    public Achievement<T> removeAchievement(@NonNull AchievementTemplate template);

    /**
     * Removes the given achievement from the holder.
     *
     * @param achievement to remove
     *
     * @return null if achievement did not exists
     *
     * @see #removeAchievement(AchievementTemplate)
     */
    public default Achievement<T> removeAchievement(@NonNull Achievement<T> achievement) {

        return removeAchievement(achievement.getTemplate());
    }

    /**
     * Saves the achievement holder by the underlying serialization method.
     */
    public void save();

    /**
     * Deletes the saved achievement holder with the underlying serialization method.
     */
    public void delete();
}
