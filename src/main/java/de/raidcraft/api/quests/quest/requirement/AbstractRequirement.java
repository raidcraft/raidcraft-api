package de.raidcraft.api.quests.quest.requirement;

import de.raidcraft.api.quests.util.QuestUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractRequirement implements Requirement {

    private final int id;
    private final String type;
    private final int requiredCount;
    private final String countText;

    public AbstractRequirement(int id, ConfigurationSection data) {

        this.id = id;
        this.type = data.getString("type");
        this.requiredCount = data.getInt("count", 0);
        this.countText = data.getString("count-text");
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getType() {

        return type;
    }

    @Override
    public int getRequiredCount() {

        return requiredCount;
    }

    @Override
    public String getCountText(int count) {

        if (countText == null || countText.equals("")) {
            return "";
        }
        return QuestUtil.replaceCount(countText, count, getRequiredCount());
    }

    @Override
    public int compareTo(Requirement o) {

        return Integer.compare(getId(), o.getId());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractRequirement)) return false;

        AbstractRequirement that = (AbstractRequirement) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {

        return id;
    }

    @Override
    public String toString() {

        return getType();
    }
}
