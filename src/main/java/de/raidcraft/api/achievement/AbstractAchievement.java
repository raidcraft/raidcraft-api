package de.raidcraft.api.achievement;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
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
    private Timestamp gainedDate;

    @SuppressWarnings("unchecked")
    public AbstractAchievement(AchievementHolder<T> holder, AchievementTemplate template) {

        this.holder = holder;
        this.template = template;
        this.applicableRequirements = template.getRequirements(holder.getType().getClass());
        this.applicableActions = template.getActions(holder.getType().getClass());
    }

    @Override
    public void unlock() {

        if (getGainedDate() != null) return;
        getHolder().addAchievement(this);
        setGainedDate(Timestamp.from(Instant.now()));
        // trigger all applicable actions
        getApplicableActions().forEach(action -> action.accept(getHolder().getType()));
        save();
    }

    @Override
    public void remove() {

        setGainedDate(null);
        getHolder().removeAchievement(this);
        save();
    }

    @Override
    public void processTrigger() {

        if (getApplicableRequirements().stream().sorted().allMatch(
                requirement -> requirement.test(getHolder().getType())
        )) {
            unlock();
        }
    }
}
