package de.raidcraft.api.quests.quest;

import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.requirement.Requirement;
import de.raidcraft.api.quests.quest.trigger.Trigger;

import java.util.List;

/**
 * @author Silthus
 */
public interface QuestTemplate {

    public String getId();

    public String getBasePath();

    public String getName();

    public String getFriendlyName();

    public String getDescription();

    public int getRequiredObjectiveAmount();

    public boolean isOrdered();

    public boolean isLocked();

    public Requirement[] getRequirements();

    public Objective[] getObjectives();

    public Trigger[] getTrigger();

    public Trigger[] getCompleteTrigger();

    public List<Action<QuestTemplate>> getCompleteActions();
}
