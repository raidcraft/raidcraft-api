package de.raidcraft.api.quests.quest;

import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.api.quests.player.QuestHolder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * @author Silthus
 */
@Getter
@Setter
public abstract class AbstractQuest implements Quest {

    private final int id;
    private final QuestTemplate template;
    private final QuestHolder holder;
    @NonNull
    private final Collection<TriggerFactory> startTrigger;
    @NonNull
    private final Collection<TriggerFactory> completionTrigger;

    private Timestamp startTime;
    private Timestamp completionTime;

    public AbstractQuest(int id, QuestTemplate template, QuestHolder holder) {

        this.id = id;
        this.template = template;
        this.holder = holder;
        this.startTrigger = template.getStartTrigger();
        this.completionTrigger = template.getCompletionTrigger();
        registerListeners();
    }

    public void registerListeners() {

        unregisterListeners();
        if (!isCompleted() && !isActive()) {
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
    public void processTrigger() {


    }

    @Override
    public QuestHolder getTriggerEntityType() {

        return getHolder();
    }

    @Override
    public boolean isCompleted() {

        return completionTime != null;
    }

    @Override
    public boolean isActive() {

        return getStartTime() != null && !isCompleted();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractQuest)) return false;

        AbstractQuest that = (AbstractQuest) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {

        return id;
    }

    @Override
    public String toString() {

        return getTemplate().toString() + "." + getHolder().toString();
    }
}
