package de.raidcraft.api.action.requirement;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionBuilder;
import de.raidcraft.api.builder.BaseBuilder;

public class RequirementBuilder<TEntity> extends BaseBuilder<Requirement<TEntity>> {

    public RequirementBuilder(Requirement<TEntity> tEntityAction) {
        super(tEntityAction);
    }

    public ActionBuilder<TEntity> withAction(Action<TEntity> action) {
        return withBuilder(ActionBuilder.class, action);
    }

    public RequirementBuilder<TEntity> withConfig(String key, Object value) {
        getResult().with(key, value);
        return this;
    }
}
