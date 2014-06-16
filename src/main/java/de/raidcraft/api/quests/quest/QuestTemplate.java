package de.raidcraft.api.quests.quest;

import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.requirement.Requirement;

import java.util.Collection;
import java.util.List;

/**
 * @author Silthus
 */
public interface QuestTemplate {

    public String getId();

    public String getBasePath();

    public String getName();

    public String getFriendlyName();

    public String getAuthor();

    public String getDescription();

    public int getRequiredObjectiveAmount();

    public boolean isOrdered();

    public boolean isLocked();

    public Requirement[] getRequirements();

    public Objective[] getObjectives();

    public Collection<TriggerFactory> getStartTrigger();

    public Collection<TriggerFactory> getCompletionTrigger();

    public List<Action<QuestTemplate>> getCompleteActions();
}
