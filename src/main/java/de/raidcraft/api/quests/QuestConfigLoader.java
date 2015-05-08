package de.raidcraft.api.quests;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Quest plugin loads all yml files in the quest folder.
 * For each type, e.g. host, conv, quest their is a different loader.
 * @author Silthus
 */
@Getter
public abstract class QuestConfigLoader implements Comparable<QuestConfigLoader> {

    private int priority = 1;
    private final String suffix;

    public QuestConfigLoader(String suffix) {

        this.suffix = ("." + suffix + ".yml").toLowerCase();
    }

    public QuestConfigLoader(String suffix, int priority) {

        this(suffix);
        this.priority = priority;
    }

    public String getSuffix() {

        return suffix;
    }

    public abstract void loadConfig(String id, ConfigurationSection config);

    public String replaceReference(String key) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof QuestConfigLoader)) return false;

        QuestConfigLoader loader = (QuestConfigLoader) o;

        return suffix.equals(loader.suffix);
    }

    @Override
    public int hashCode() {

        return suffix.hashCode();
    }

    @Override
    public int compareTo(QuestConfigLoader o) {

        return Integer.compare(getPriority(), o.getPriority());
    }
}
