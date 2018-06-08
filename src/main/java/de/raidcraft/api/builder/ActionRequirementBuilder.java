package de.raidcraft.api.builder;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionBuilder;
import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementBuilder;
import de.raidcraft.api.action.requirement.RequirementHolder;
import lombok.Data;

import java.util.function.Consumer;

@Data
public class ActionRequirementBuilder<TParent, TResult extends ActionHolder & RequirementHolder> extends BaseBuilder<TParent, TResult> {

    public ActionRequirementBuilder(TParent parent, TResult item) {
        super(parent, item);
    }

    public <TEntity> ActionRequirementBuilder<TParent, TResult> withAction(Action<TEntity> action, Consumer<ActionBuilder> callback) {

        ActionBuilder<?, Action<TEntity>> actionBuilder = withBuilder(ActionBuilder.class, action, callback);
        actionBuilder.build(result -> getResult().addAction(result));

        return this;
    }

    public <TEntity> ActionRequirementBuilder<TParent, TResult> withAction(Action<TEntity> action) {
        return withAction(action, null);
    }

    public <TEntity> ActionRequirementBuilder<TParent, TResult> withRequirement(Requirement<TEntity> requirement, Consumer<RequirementBuilder> callback) {

        RequirementBuilder<?, Requirement<TEntity>> requirementBuilder = withBuilder(RequirementBuilder.class, requirement, callback);
        requirementBuilder.build(result -> getResult().addRequirement(result));
        return this;
    }

    public <TEntity> ActionRequirementBuilder<TParent, TResult> withRequirement(Requirement<TEntity> requirement) {
        return withRequirement(requirement, null);
    }
}
