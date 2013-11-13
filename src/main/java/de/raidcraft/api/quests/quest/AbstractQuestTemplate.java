package de.raidcraft.api.quests.quest;

import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.requirement.Requirement;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractQuestTemplate implements QuestTemplate {

    private final String id;
    private final String name;
    private final String basePath;
    private final String friendlyName;
    private final String description;
    private final int requiredObjectiveAmount;
    private final boolean ordered;
    private final boolean locked;
    private List<Action<QuestTemplate>> actions = new ArrayList<>();
    protected Requirement[] requirements = new Requirement[0];
    protected Objective[] objectives = new Objective[0];
    protected Trigger[] trigger = new Trigger[0];
    protected Trigger[] completeTrigger = new Trigger[0];

    public AbstractQuestTemplate(String id, ConfigurationSection data) {

        this.id = id;
        String[] split = id.split("\\.");
        this.name = split[split.length - 1];
        this.basePath = id.replace("." + name, "");
        this.friendlyName = data.getString("name", name);
        this.description = data.getString("desc");
        this.requiredObjectiveAmount = data.getInt("required", 0);
        this.ordered = data.getBoolean("ordered", false);
        this.locked = data.getBoolean("locked", true);
        loadRequirements(data.getConfigurationSection("requirements"));
        loadObjectives(data.getConfigurationSection("objectives"));
        loadTrigger(data.getConfigurationSection("trigger"));
        loadCompleteTrigger(data.getConfigurationSection("complete-trigger"));
        loadActions(data.getConfigurationSection("complete-actions"));
    }

    protected abstract void loadRequirements(ConfigurationSection data);

    protected abstract void loadObjectives(ConfigurationSection data);

    protected abstract void loadTrigger(ConfigurationSection data);

    protected abstract void loadCompleteTrigger(ConfigurationSection data);

    protected abstract void loadActions(ConfigurationSection data);

    @Override
    public String getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getBasePath() {

        return basePath;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public int getRequiredObjectiveAmount() {

        return requiredObjectiveAmount;
    }

    @Override
    public boolean isOrdered() {

        return ordered;
    }

    @Override
    public Requirement[] getRequirements() {

        return requirements;
    }

    @Override
    public Objective[] getObjectives() {

        return objectives;
    }

    @Override
    public Trigger[] getTrigger() {

        return trigger;
    }

    @Override
    public Trigger[] getCompleteTrigger() {

        return completeTrigger;
    }

    @Override
    public List<Action<QuestTemplate>> getCompleteActions() {

        return actions;
    }

    protected void setActions(List<Action<QuestTemplate>> actions) {

        this.actions = actions;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractQuestTemplate)) return false;

        AbstractQuestTemplate that = (AbstractQuestTemplate) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {

        return id.hashCode();
    }

    @Override
    public String toString() {

        return id;
    }
}
