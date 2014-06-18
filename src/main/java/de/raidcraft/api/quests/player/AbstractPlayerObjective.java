package de.raidcraft.api.quests.player;

import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.api.action.trigger.TriggerManager;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * @author Silthus
 */
@Getter
@Setter
public abstract class AbstractPlayerObjective implements PlayerObjective {

    private final int id;
    private final Quest quest;
    private final Objective objective;
    private final Collection<TriggerFactory> triggerFactories;
    private Timestamp completionTime;

    public AbstractPlayerObjective(int id, Quest quest, Objective objective) {

        this.id = id;
        this.quest = quest;
        this.objective = objective;
        this.triggerFactories = getObjective().gT
        if (!isCompleted()) {
            // lets register ourselves as trigger listener
            for (Trigger trigger : getObjective().getTrigger()) {
                trigger.registerListener(this);
            }
        }
    }

    public void registerListeners() {

        if (!isCompleted()) {
            // register our start trigger
            startTrigger.forEach(factory -> factory.registerListener(this));
        } else if (hasCompletedAllObjectives() && isActive()) {
            // register the completion trigger
            completionTrigger.forEach(factory -> factory.registerListener(this));
        }
    }

    public void unregisterListeners() {

        startTrigger.forEach(factory -> factory.unregisterListener(this));
        completionTrigger.forEach(factory -> factory.unregisterListener(this));
    }

    @Override
    public QuestHolder getQuestHolder() {

        return quest.getHolder();
    }

    @Override
    public boolean isCompleted() {

        return completionTime != null;
    }

    protected void setCompleted(Timestamp timestamp) {

        this.completionTime = timestamp;
        if (isCompleted()) {
            // unregister ourselves as trigger listener
            for (Trigger trigger : getObjective().getTrigger()) {
                trigger.unregisterListener(this);
            }
        }
    }

    @Override
    public int compareTo(PlayerObjective o) {

        return getObjective().compareTo(o.getObjective());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractPlayerObjective)) return false;

        AbstractPlayerObjective that = (AbstractPlayerObjective) o;

        return id == that.id && objective.equals(that.objective) && quest.equals(that.quest);
    }

    @Override
    public int hashCode() {

        int result = id;
        result = 31 * result + quest.hashCode();
        result = 31 * result + objective.hashCode();
        return result;
    }

    @Override
    public String toString() {

        return getQuestHolder().getName() + "." + getObjective().toString();
    }
}
