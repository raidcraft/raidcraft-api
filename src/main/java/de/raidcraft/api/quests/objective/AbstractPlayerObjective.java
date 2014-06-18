package de.raidcraft.api.quests.objective;

import de.raidcraft.api.quests.holder.QuestHolder;
import de.raidcraft.api.quests.quest.Quest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
@Data
@EqualsAndHashCode(of = {"id", "quest", "objectiveTemplate"})
public abstract class AbstractPlayerObjective implements PlayerObjective {

    private final int id;
    private final Quest quest;
    private final ObjectiveTemplate objectiveTemplate;
    private Timestamp completionTime;

    public AbstractPlayerObjective(int id, Quest quest, ObjectiveTemplate objectiveTemplate) {

        this.id = id;
        this.quest = quest;
        this.objectiveTemplate = objectiveTemplate;
        registerListeners();
    }

    @Override
    public void processTrigger() {

        if (getObjectiveTemplate().getRequirements().stream().allMatch(requirement -> requirement.test(getQuestHolder().getPlayer()))) {
            complete();
        }
    }

    public void registerListeners() {

        if (!isCompleted()) {
            // register our start trigger
            getObjectiveTemplate().getTrigger().forEach(factory -> factory.registerListener(this));
        } else {
            unregisterListeners();
            // pass back the listener registration to the quest
            getQuest().updateObjectiveListeners();
        }
    }

    public void unregisterListeners() {

        getObjectiveTemplate().getTrigger().forEach(factory -> factory.unregisterListener(this));
    }

    @Override
    public QuestHolder getQuestHolder() {

        return quest.getHolder();
    }

    @Override
    public boolean isCompleted() {

        return completionTime != null;
    }

    @Override
    public void complete() {

        this.completionTime = new Timestamp(System.currentTimeMillis());
        unregisterListeners();
        getQuest().onObjectCompletion(this);
    }

    @Override
    public int compareTo(PlayerObjective o) {

        return this.getObjectiveTemplate().compareTo(o.getObjectiveTemplate());
    }
}
