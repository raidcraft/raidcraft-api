package de.raidcraft.api.action.action;

import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementBuilder;
import de.raidcraft.api.builder.BaseBuilder;

public class ActionBuilder<TEntity> extends BaseBuilder<Action<TEntity>> {

    public ActionBuilder(Action<TEntity> tEntityAction) {
        super(tEntityAction);
    }

    public RequirementBuilder<TEntity> withRequirement(Requirement<TEntity> requirement) {
        return withBuilder(RequirementBuilder.class, requirement);
    }

    public ActionBuilder<TEntity> withConfig(String key, Object value) {
        getResult().with(key, value);

        return this;
    }
}
