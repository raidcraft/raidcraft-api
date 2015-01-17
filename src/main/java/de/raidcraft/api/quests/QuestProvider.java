package de.raidcraft.api.quests;

import de.raidcraft.api.quests.holder.QuestHolder;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface QuestProvider {

    public void registerQuestHost(String type, Class<? extends QuestHost> clazz) throws InvalidQuestHostException;

    public void registerQuestConfigLoader(QuestConfigLoader loader) throws QuestException;

    public QuestHost getQuestHost(String id) throws InvalidQuestHostException;

    public QuestHolder getQuestHolder(Player player);
}
