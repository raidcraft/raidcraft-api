package de.raidcraft.api.quests.quest.objective;

import de.raidcraft.api.quests.quest.requirement.Requirement;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.trigger.Trigger;

import java.util.List;

/**
 * @author Silthus
 */
public interface Objective extends Comparable<Objective> {

    public int getId();

    public String getFriendlyName();

    public String getDescription();

    public boolean isOptional();

    public QuestTemplate getQuestTemplate();

    public Requirement[] getRequirements();

    public Trigger[] getTrigger();

    public List<Action<Objective>> getActions();
}
