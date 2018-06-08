package de.raidcraft.api.action.action;

import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementBuilder;
import de.raidcraft.api.builder.BaseBuilder;

public class ActionBuilder<TParent, TEntity> extends BaseBuilder<TParent, Action<TEntity>> {

    public ActionBuilder(TParent tParent, Action<TEntity> tEntityAction) {
        super(tParent, tEntityAction);
    }

    public RequirementBuilder<TParent, TEntity> withRequirement(Requirement<TEntity> requirement) {
        return withBuilder(RequirementBuilder.class, requirement);
    }

    public ActionBuilder<TParent, TEntity> withConfig(String key, Object value) {
        getResult().with(key, value);
        return this;
    }
}
