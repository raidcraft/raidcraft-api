package de.raidcraft.api.achievement;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
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
    @Setter(AccessLevel.PROTECTED)
    private String description = "";

    public AbstractAchievementTemplate(String identifier) {

        this(identifier, identifier);
    }

    public AbstractAchievementTemplate(String identifier, String displayName) {

        this.identifier = identifier;
        this.displayName = displayName;
        this.requirements = loadRequirements();
        this.actions = loadActions();
    }

    protected abstract Collection<Requirement<?>> loadRequirements();

    protected abstract Collection<Action<?>> loadActions();
}
