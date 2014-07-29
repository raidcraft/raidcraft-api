package de.raidcraft.api.quests.objective;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.util.QuestUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Silthus
 */
@Data
@EqualsAndHashCode(of = {"id", "questTemplate"})
@ToString(exclude = {"requirements", "trigger", "actions"})
public abstract class AbstractObjectiveTemplate implements ObjectiveTemplate {

    private final int id;
    private final String friendlyName;
    private final String description;
    private final boolean optional;
    private final QuestTemplate questTemplate;
    private final Collection<Requirement<Player>> requirements;
    private final Collection<TriggerFactory> trigger;
    private final Collection<Action<Player>> actions;

    public AbstractObjectiveTemplate(int id, QuestTemplate questTemplate, ConfigurationSection data) {

        this.id = id;
        this.friendlyName = QuestUtil.replaceRefrences(questTemplate.getBasePath(), data.getString("name"));
        this.description = QuestUtil.replaceRefrences(questTemplate.getBasePath(), data.getString("description"));
        this.optional = data.getBoolean("optional", false);
        this.questTemplate = questTemplate;
        this.requirements = loadRequirements(data.getConfigurationSection("requirements"));
        this.trigger = loadTrigger(data.getConfigurationSection("trigger"));
        this.actions = loadActions(data.getConfigurationSection("actions"));
    }

    protected abstract Collection<Requirement<Player>> loadRequirements(ConfigurationSection data);

    protected abstract Collection<TriggerFactory> loadTrigger(ConfigurationSection data);

    protected abstract Collection<Action<Player>> loadActions(ConfigurationSection data);

    @Override
    public int compareTo(@NonNull ObjectiveTemplate o) {

        if (getId() < o.getId()) {
            return -1;
        }
        if (getId() > o.getId()) {
            return 1;
        }
        return 0;
    }
}
