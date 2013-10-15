package de.raidcraft.api.quests;

import de.raidcraft.api.quests.player.QuestHolder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Silthus
 */
public interface QuestProvider {

    public void registerQuestType(JavaPlugin plugin, QuestType actionType) throws InvalidTypeException;

    public void registerQuestHost(String type, Class<? extends QuestHost> clazz) throws InvalidQuestHostException;

    public QuestHost getQuestHost(String id) throws InvalidQuestHostException;

    public QuestHolder getQuestHolder(Player player);
}
