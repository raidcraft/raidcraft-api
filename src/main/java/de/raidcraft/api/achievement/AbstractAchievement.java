package de.raidcraft.api.achievement;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.achievement.events.AchievementGainEvent;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
@ToString(of = {"holder", "template"})
@EqualsAndHashCode(of = {"holder", "template"})
@Data
public abstract class AbstractAchievement<T> implements Achievement<T> {

    @NonNull
    private final AchievementHolder<T> holder;
    @NonNull
    private final AchievementTemplate template;
    @NonNull
    private final Collection<Requirement<T>> applicableRequirements;
    @NonNull
    private final Collection<Action<T>> applicableActions;
    @NonNull
    private final Collection<TriggerFactory> triggerFactories;
    private Timestamp completionDate;

    @SuppressWarnings("unchecked")
    public AbstractAchievement(AchievementHolder<T> holder, AchievementTemplate template) {

        this.holder = holder;
        this.template = template;
        this.applicableRequirements = template.getRequirements(holder.getType().getClass());
        this.applicableActions = template.getActions(holder.getType().getClass());
        this.triggerFactories = template.getTrigger();
        // enable all trigger listeners
        registerListeners();
    }

    @Override
    public String getListenerId() {

        return getIdentifier();
    }

    @Override
    public void registerListeners() {

        if (isCompleted() || !isActive()) return;
        triggerFactories.forEach(factory -> factory.registerListener(this));
    }

    @Override
    public void unregisterListeners() {

        triggerFactories.forEach(factory -> factory.unregisterListener(this));
    }

    @Override
    public boolean unlock() {

        if (!getTemplate().isEnabled() && !getHolder().hasPermission("rcachievement.ignore-disabled")) return false;
        // disable all trigger listener
        unregisterListeners();
        // check if achievement is already unlocked
        if (getCompletionDate() != null) return false;
        // inform other plugins that the holder gained an achievement
        AchievementGainEvent event = new AchievementGainEvent(this);
        RaidCraft.callEvent(event);

        getHolder().addAchievement(this);
        this.setCompletionDate(Timestamp.from(Instant.now()));
        // trigger all applicable actions
        getApplicableActions().forEach(action -> action.accept(getHolder().getType()));
        save();
        return true;
    }

    @Override
    public void remove() {

        // disable all trigger listener
        unregisterListeners();

        this.setCompletionDate(null);
        getHolder().removeAchievement(this);
        save();
    }

    @Override
    public boolean processTrigger(T entity) {

        if (!entity.equals(getHolder().getType())) {
            return false;
        }
        if (!getTemplate().isEnabled() && !getHolder().hasPermission("rcachievement.ignore-disabled")) {
            return false;
        }
        if (getApplicableRequirements().isEmpty()) {
            unlock();
            return true;
        }

        // first lets filter out all ordered and unordered requirements
        List<Requirement<T>> orderedRequirements = getApplicableRequirements().stream()
                .filter(Requirement::isOrdered)
                .sorted()
                .collect(Collectors.toList());
        List<Requirement<T>> unorderedRequirements = getApplicableRequirements().stream()
                .filter(req -> !req.isOrdered())
                .collect(Collectors.toList());

        boolean allMatch = true;
        // now we go thru all unordered requirements and test them
        for (Requirement<T> requirement : unorderedRequirements) {
            boolean test = requirement.test(entity);
            // only set to false and not back to true
            // also dont set to false when the requirement is optional
            if (allMatch && !requirement.isOptional()) {
                allMatch = test;
            }
        }

        // we can check the ordered requirements via stream
        // since the stream aborts as soon one does not match
        allMatch = orderedRequirements.stream().allMatch(requirement -> requirement.test(entity));

        return allMatch && unlock();
    }
}
