package de.raidcraft.api.quests;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Silthus
 */
public interface QuestProvider {

    public void registerQuestType(JavaPlugin plugin, QuestType actionType) throws InvalidTypeException;

    public void callTrigger(QuestTrigger trigger, Player player);
}
