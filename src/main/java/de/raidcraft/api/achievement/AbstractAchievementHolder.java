package de.raidcraft.api.achievement;

import lombok.Data;
import lombok.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mdoering
 */
@Data
public abstract class AbstractAchievementHolder<T> implements AchievementHolder<T> {

    @NonNull
    private final T type;
    @NonNull
    private final Map<String, Achievement<T>> achievements = new HashMap<>(); // TODO: make case insenstive

    public AbstractAchievementHolder(T type) {

        this.type = type;
    }

    @Override
    public boolean hasAchievement(AchievementTemplate template) {

        return template != null && achievements.containsKey(template.getIdentifier());
    }

    @Override
    public Collection<Achievement<T>> getAchievements() {

        return achievements.values();
    }

    @Override
    public Achievement<T> removeAchievement(AchievementTemplate template) {

        Achievement<T> achievement = achievements.remove(template.getIdentifier());
        if (achievement != null) {
            achievement.remove();
        }
        return achievement;
    }
}
