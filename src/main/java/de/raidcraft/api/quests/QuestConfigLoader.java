package de.raidcraft.api.quests;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class QuestConfigLoader {

    private final String suffix;

    public QuestConfigLoader(String suffix) {

        this.suffix = ("." + suffix + ".yml").toLowerCase();
    }

    public String getSuffix() {

        return suffix;
    }

    public abstract void loadConfig(String id, ConfigurationSection config);

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
}
