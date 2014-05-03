package de.raidcraft.api.achievement;

import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.action.requirement.RequirementHolder;
import de.raidcraft.api.action.trigger.TriggerHolder;

/**
 * The achievement template contains generic information about the achievement.
 * It has no information about the current progress or if it was completed.
 * The achievement templates should be loaded and popuplated on server startup.
 * Depending on the implementation templates can be generated from a database,
 * file system or any other source.
 */
public interface AchievementTemplate extends RequirementHolder, ActionHolder, TriggerHolder {

    /**
     * Gets the unique name of the Achievement that can be used to compare
     * or filter it. The unique name is based off the folder structure and file name.
     *
     * @return unique name of the achievement
     */
    public String getIdentifier();

    /**
     * Gets the friendly display name of the achievement as it is displayed
     * to the player and in text messages and outputs.
     *
     * @return friendly display name
     */
    public String getDisplayName();

    /**
     * Gets the description of the achievement. The description may contain
     * information about how to get the achievement and hints for the players.
     *
     * @return achievement description
     */
    public String getDescription();

    /**
     * Gets the amount of points the achievement is worth. This has no effect
     * and is only for toplist calculations and e-penis comparisons :).
     *
     * @return achievement points
     */
    public int getPoints();

    /**
     * Sets the achievement as enabled allowing it to be gained by players.
     * This will also enable the listening to triggers for active achievements.
     *
     * @param enabled false to lock achievement
     */
    public void setEnabled(boolean enabled);

    /**
     * Checks if the achievement is enabled and can be gained by players.
     * A new achievement must first be enabled before it can be used.
     * Players with the permission "rcachievements.ignore-disabled" will also
     * be able to gain disabled achievements.
     *
     * @return true if enabled. default is false
     */
    public boolean isEnabled();

    /**
     * Sets the achievement as secret achievements. Secret achievements dont display their
     * name and description but still get broadcasted with their name when gained.
     *
     * @param secret true will hide the name and description in the overview
     */
    public void setSecret(boolean secret);

    /**
     * Checks if the name and description of the achievement are hidden in the overview.
     *
     * @return true if achievement is secret. default is false
     */
    public boolean isSecret();

    /**
     * Sets the achievement to broadcast to all players when someone gains the achievement.
     * The default is that all achievements are broadcasted.
     *
     * @param broadcasting false disables broadcasting of the achievement
     */
    public void setBroadcasting(boolean broadcasting);

    /**
     * Checks if the achievement is broadcasting to all players when someone gains it.
     *
     * @return true if broadcasting is enabled. default is true
     */
    public boolean isBroadcasting();

    /**
     * Creates a valid achievement from this template and the given holder.
     *
     * @param holder to create achievement for
     * @param <T> type of the holder
     * @return active achievement
     */
    public <T> Achievement<T> createAchievement(AchievementHolder<T> holder);
}
