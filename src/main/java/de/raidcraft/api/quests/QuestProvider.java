package de.raidcraft.api.quests;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Silthus
 */
public interface QuestProvider {

    public void registerQuestType(JavaPlugin plugin, QuestType actionType) throws InvalidTypeException;

    public void registerQuestHost(String type, Class<? extends QuestHost> clazz) throws InvalidQuestHostException;

    public String getFriendlyHostName(String id);

    public void callTrigger(String name, Player player);
}
