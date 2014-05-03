package de.raidcraft.api.achievement;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.achievement.events.AchievementGainEvent;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerFactory;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

/**
 * @author mdoering
 */
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
    private Timestamp gainedDate;

    @SuppressWarnings("unchecked")
    public AbstractAchievement(AchievementHolder<T> holder, AchievementTemplate template) {

        this.holder = holder;
        this.template = template;
        this.applicableRequirements = template.getRequirements(holder.getType().getClass());
        this.applicableActions = template.getActions(holder.getType().getClass());
        this.triggerFactories = template.getTrigger();
        // enable all trigger listeners
        triggerFactories.forEach(factory -> factory.registerListener(this));
    }

    @Override
    public void unlock() {

        if (!getTemplate().isEnabled() && !getHolder().hasPermission("rcachievement.ignore-disabled")) return;
        // disable all trigger listener
        triggerFactories.forEach(factory -> factory.unregisterListener(this));
        // check if achievement is already unlocked
        if (getGainedDate() != null) return;
        // inform other plugins that the holder gained an achievement
        AchievementGainEvent event = new AchievementGainEvent(this);
        RaidCraft.callEvent(event);

        getHolder().addAchievement(this);
        setGainedDate(Timestamp.from(Instant.now()));
        // trigger all applicable actions
        getApplicableActions().forEach(action -> action.accept(getHolder().getType()));
        save();
    }

    @Override
    public void remove() {

        // disable all trigger listener
        triggerFactories.forEach(factory -> factory.unregisterListener(this));

        setGainedDate(null);
        getHolder().removeAchievement(this);
        save();
    }

    @Override
    public void processTrigger() {

        if (!getTemplate().isEnabled() && !getHolder().hasPermission("rcachievement.ignore-disabled")) {
            return;
        }
        if (getApplicableRequirements().isEmpty() || getApplicableRequirements().stream().sorted().allMatch(
                requirement -> requirement.test(getHolder().getType())
        )) {
            unlock();
        }
    }
}
