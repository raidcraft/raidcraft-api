package de.raidcraft.api.quests.player;

import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.trigger.TriggerListener;
import de.raidcraft.api.quests.quest.Quest;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public interface PlayerObjective extends TriggerListener, Comparable<PlayerObjective> {

    public int getId();

    public Quest getQuest();

    public Objective getObjective();

    public Player getPlayer();

    public Timestamp getCompletionTime();

    public boolean isCompleted();

    public void save();
}
