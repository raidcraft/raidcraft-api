package de.raidcraft.api.quests.quest.requirement;

import de.raidcraft.api.quests.QuestException;
import org.bukkit.entity.Player;

/**
 * @deprecated see {@link de.raidcraft.api.action.requirement.Requirement}
 */
@Deprecated
public interface Requirement extends Comparable<Requirement> {

    public int getId();

    public String getType();

    public int getRequiredCount();

    public String getCountText(int count);

    public boolean test(Player player) throws QuestException;
}
