package de.raidcraft.api.quests.quest;

import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.api.quests.player.PlayerObjective;
import de.raidcraft.api.quests.player.QuestHolder;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Silthus
 */
public interface Quest extends TriggerListener<QuestHolder> {

    public int getId();

    public default String getName() {

        return getTemplate().getName();
    }

    public default String getFullName() {

        return getTemplate().getId();
    }

    public default String getFriendlyName() {

        return getTemplate().getFriendlyName();
    }

    public default String getAuthor() {

        return getTemplate().getAuthor();
    }

    public default String getDescription() {

        return getTemplate().getDescription();
    }

    public List<PlayerObjective> getPlayerObjectives();

    public List<PlayerObjective> getUncompletedObjectives();

    public QuestTemplate getTemplate();

    public QuestHolder getHolder();

    public default Player getPlayer() {

        return getHolder().getPlayer();
    }

    public boolean isCompleted();

    public boolean hasCompletedAllObjectives();

    public void completeObjective(PlayerObjective objective);

    public boolean isActive();

    public Timestamp getStartTime();

    public Timestamp getCompletionTime();

    public void start();

    public void complete();

    public void abort();

    public void save();
}
