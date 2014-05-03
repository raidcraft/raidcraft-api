package de.raidcraft.api.achievement;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerFactory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import java.util.Collection;

/**
 * @author mdoering
 */
@Data
public abstract class AbstractAchievementTemplate implements AchievementTemplate {

    @NonNull
    private final String identifier;
    @NonNull
    private final String displayName;
    @NonNull
    private final Collection<Requirement<?>> requirements;
    @NonNull
    private final Collection<Action<?>> actions;
    @NonNull
    private final Collection<TriggerFactory> trigger;
    @Setter(AccessLevel.PROTECTED)
    private String description = "";
    private boolean enabled = true;
    private boolean secret = false;
    private boolean broadcasting = true;

    public AbstractAchievementTemplate(String identifier) {

        this(identifier, identifier);
    }

    public AbstractAchievementTemplate(String identifier, String displayName) {

        this.identifier = identifier;
        this.displayName = displayName;
        this.requirements = loadRequirements();
        this.actions = loadActions();
        this.trigger = loadTrigger();
    }

    protected abstract Collection<Requirement<?>> loadRequirements();

    protected abstract Collection<Action<?>> loadActions();

    protected abstract Collection<TriggerFactory> loadTrigger();
}
