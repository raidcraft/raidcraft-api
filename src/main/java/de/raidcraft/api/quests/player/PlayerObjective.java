package de.raidcraft.api.quests.player;

import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.api.quests.quest.objective.Objective;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public interface PlayerObjective extends TriggerListener<Player>, Comparable<PlayerObjective> {

    public int getId();

    public Quest getQuest();

    public Objective getObjective();

    public QuestHolder getQuestHolder();

    public Timestamp getCompletionTime();

    public boolean isCompleted();

    public void save();
}
