package de.raidcraft.api.quests.objective;

import de.raidcraft.api.action.trigger.TriggerListener;
import de.raidcraft.api.quests.holder.QuestHolder;
import de.raidcraft.api.quests.quest.Quest;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public interface PlayerObjective extends TriggerListener<Player>, Comparable<PlayerObjective> {

    @Override
    public default Player getTriggerEntityType() {

        return getQuestHolder().getPlayer();
    }

    public int getId();

    public Quest getQuest();

    public ObjectiveTemplate getObjectiveTemplate();

    public QuestHolder getQuestHolder();

    public Timestamp getCompletionTime();

    public boolean isCompleted();

    public void complete();

    public void registerListeners();

    public void unregisterListeners();

    public void save();
}
