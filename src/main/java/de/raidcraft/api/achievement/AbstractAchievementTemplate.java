package de.raidcraft.api.achievement;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerFactory;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author mdoering
 */
@EqualsAndHashCode(of = {"identifier"})
@Data
public abstract class AbstractAchievementTemplate implements AchievementTemplate {

    @NonNull
    private final String identifier;
    @NonNull
    private final String displayName;
    @NonNull
    private Collection<Requirement<?>> requirements = new ArrayList<>();
    @NonNull
    private Collection<Action<?>> actions = new ArrayList<>();
    @NonNull
    private Collection<TriggerFactory> trigger = new ArrayList<>();
    @Setter(AccessLevel.PROTECTED)
    private String description = "";
    private int points = 10;
    private boolean enabled = true;
    private boolean secret = false;
    private boolean broadcasting = true;

    public AbstractAchievementTemplate(String identifier) {

        this(identifier, identifier);
    }

    public AbstractAchievementTemplate(String identifier, String displayName) {

        this.identifier = identifier;
        this.displayName = displayName;
    }

    protected abstract Collection<Requirement<?>> loadRequirements();

    protected abstract Collection<Action<?>> loadActions();

    protected abstract Collection<TriggerFactory> loadTrigger();
}
