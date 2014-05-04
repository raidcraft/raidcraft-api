package de.raidcraft.api.achievement;

import de.raidcraft.util.CaseInsensitiveMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.util.Collection;
import java.util.Map;

/**
 * @author mdoering
 */
@ToString(of = {"type"})
@EqualsAndHashCode(of = {"type"})
@Data
public abstract class AbstractAchievementHolder<T> implements AchievementHolder<T> {

    @NonNull
    private final T type;
    @NonNull
    private Map<String, Achievement<T>> achievements = new CaseInsensitiveMap<>();

    public AbstractAchievementHolder(T type) {

        this.type = type;
        this.achievements = loadAchievements();
    }

    @NonNull
    protected abstract CaseInsensitiveMap<Achievement<T>> loadAchievements();

    @Override
    public int getTotalPoints() {

        return getCompletedAchievements().stream()
                .mapToInt(achievement -> achievement.getTemplate().getPoints())
                .sum();
    }

    @Override
    public boolean hasAchievement(@NonNull AchievementTemplate template) {

        return achievements.containsKey(template.getIdentifier());
    }

    @Override
    public Collection<Achievement<T>> getAchievements() {

        return achievements.values();
    }

    @Override
    public Achievement<T> getAchievement(String identifier) {

        return achievements.get(identifier);
    }

    @Override
    public Achievement<T> addAchievement(@NonNull AchievementTemplate template) {

        return addAchievement(template.createAchievement(this));
    }

    @Override
    public Achievement<T> addAchievement(@NonNull Achievement<T> achievement) {

        achievements.remove(achievement.getIdentifier());
        achievements.put(achievement.getIdentifier(), achievement);
        save();
        return achievement;
    }

    @Override
    public Achievement<T> removeAchievement(@NonNull AchievementTemplate template) {

        Achievement<T> achievement = achievements.remove(template.getIdentifier());
        if (achievement != null) {
            achievement.remove();
            save();
        }
        return achievement;
    }
}
