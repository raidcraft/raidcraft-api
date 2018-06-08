package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionBuilder;
import de.raidcraft.api.builder.BaseBuilder;

public class RequirementBuilder<TParent, TEntity> extends BaseBuilder<TParent, Requirement<TEntity>> {

    public RequirementBuilder(TParent tParent, Requirement<TEntity> tEntityAction) {
        super(tParent, tEntityAction);
    }

    public ActionBuilder<TParent, TEntity> withAction(Action<TEntity> action) {
        return withBuilder(ActionBuilder.class, action);
    }

    public RequirementBuilder<TParent, TEntity> withConfig(String key, Object value) {
        getResult().with(key, value);
        return this;
    }
}
